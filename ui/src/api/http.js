/* eslint-disable operator-linebreak */
import axios from 'axios'
import config from '@/config'

// 外部文件使用 pinia
import { useUserStore } from '@/stores/user'

axios.defaults.withCredentials = true
const regex = new RegExp(
  '^http[s]?://[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+.?(:[0-9]+)?'
)

class http {
  /**
   * 获取存储的token信息
   * @returns
   */
  getTokenVal() {
    const token = this.userStore.tokenVal
    if (typeof token === 'undefined') {
      return ''
    }
    return token
  }

  /**
   * 获取请求的请求头信息
   */
  headers(options) {
    const token = this.getTokenVal()
    let headers = typeof options.headers === 'undefined' ? {} : options.headers
    if (token.length === 0) {
      return headers
    }
    // debugger
    return Object.assign(headers, {
      Authorization: `xtoken ${token}`
    })
  }

  protocolAndDomainAndPort(url) {
    try {
      if (!url.match('^http[s]?://')) {
        return ''
      }
      const protocolAndDomainAndPort = url.match(/^http[s]?:\/\/[^/]+/i)[0]
      return protocolAndDomainAndPort
    } catch (e) {
      console.log(e)
    }
    return ''
  }

  /**
   * 从url中获取协议+域名+端口信息
   * @param {*} options
   */
  baseUrl(url) {
    let baseUrl = this.protocolAndDomainAndPort(url)
    if (baseUrl.length != 0) {
      // 请求的url是一个完整的url,包含了协议和域名
      return baseUrl
    }
    const dev = process.env.NODE_ENV === 'development'
    if (url.match(/^\/datawh-sql/gi)) {
      if (dev) {
        // url 为 datawh-sql 开头
        return 'http://10.8.19.7:8260'
      }
    } else if (url.match(/^\/idea/gi)) {
      // url 为 datawh-sql 开头
      return 'http://10.8.19.7:8080'
    }
    baseUrl = dev ? config.baseUrl.dev : config.baseUrl.prod
    return baseUrl
  }

  /**
   * 获取真正的url
   * 并在请求的URL后面携带上token信息
   * @param {*} options
   */
  url(url) {
    let baseUrl = this.protocolAndDomainAndPort(url)
    const uri = url.replace(baseUrl, '').trim()

    return uri
    // const token = this.getTokenVal()
    // if (token.length === 0) {
    //   return url
    // }
    // if (url.indexOf('?') != -1) {
    //   return url + `&xtoken=${token}`
    // }
    // return url + `?xtoken=${token}`
  }

  /**
   * 开始真正的请求
   * @param {*} options
   */
  request(options) {
    //定义一个全局的请求头
    const url = options.url

    const uri = this.url(url)
    const baseUrl = this.baseUrl(url)

    // console.log('======> url = ' + url + '  baseUrl = ' + baseUrl + ' uri= ' + uri)

    options.url = uri
    options.method =
      typeof options.method === 'undefined' || options.method.trim().length === 0
        ? 'get'
        : options.method

    const config = {
      baseURL: baseUrl,
      headers: this.headers(options)
    }
    const instance = axios.create()
    options = Object.assign(config, options)
    this.interceptors(instance, options.url)
    return instance(options)
  }

  destroy(url) {
    delete this.queue[url]
    if (!Object.keys(this.queue).length) {
      // Spin.hide()
    }
  }

  // 拦截器
  interceptors(instance, url) {
    const that = this
    // 请求拦截
    instance.interceptors.request.use(
      (config) => {
        // 添加全局的loading...
        if (!Object.keys(this.queue).length) {
          // Spin.show() // 不建议开启，因为界面不友好
        }
        this.queue[url] = true
        return config
      },
      (error) => {
        return Promise.reject(error)
      }
    )
    // 响应拦截
    instance.interceptors.response.use(
      (res) => {
        this.destroy(url)
        const { data, status } = res
        // return { data, status };
        // if (data.code !== 200) {
        //   this.$Message.error(data.msg);
        //   return false;
        // }
        if (status !== 200) {
          data.code = status
        }
        if (data.code === 401) {
          that.message.warn(data.msg, 5, () => {
            that.router.push({ name: 'login' })
          })
          return Promise.reject(Promise.reject(data.msg))
        }

        return data
        // return status === 200 ? data : { code: status };
      },
      (error) => {
        this.destroy(url)
        return Promise.reject(error)
      }
    )
  }

  constructor(pinia, router, message) {
    this.userStore = useUserStore(pinia)
    this.router = router
    this.queue = {}
    this.message = message
  }
}

export default http
