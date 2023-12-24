/* eslint-disable operator-linebreak */
import axios from 'axios'
import config from '@/config'

// 外部文件使用 pinia
import { useUserStore } from '@/stores/user'

axios.defaults.withCredentials = true

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
   * 提取请求的域名和端口
   * @param {*} url
   * @returns
   */
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
   * 获取请求的请求头信息
   */
  headers(options) {
    const token = this.getTokenVal()
    let headers = typeof options.headers === 'undefined' ? {} : options.headers
    if (token.length === 0) {
      return headers
    }
    const currentRoleId = this.userStore.currentRoleId
    return Object.assign(headers, {
      Authorization: `xtoken ${token}`,
      role: `${currentRoleId}`
    })
  }

  /**
   * 开始真正的请求
   * @param {*} options
   */
  request(options) {
    //定义一个全局的请求头

    options.url = this.url(options.url)
    options.method =
      typeof options.method === 'undefined' || options.method.trim().length === 0
        ? 'post'
        : options.method

    const config = {
      baseURL: this.baseUrl(options.url),
      headers: this.headers(options)
    }
    options = Object.assign(config, options)
    const instance = axios.create()
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

        if (status !== 200) {
          // 请求处理失败
          data.code = status
        }
        if (data.code !== 200) {
          if (data.code === 401) {
            this.modal.error({
              title: '未经授权',
              content: '您还未登陆或登陆状态已过,请重新登录后再访问此问题',
              onOk() {
                that.router.push({
                  name: 'login'
                })
              },
              onCancel() {
                that.router.push({
                  name: 'login'
                })
              }
            })
          } else {
            // 非401
            that.message.error(data.msg)
          }

          return Promise.reject(data.msg)
        }

        return !data.data ? {} : data.data
      },
      (error) => {
        this.destroy(url)
        return Promise.reject(error)
      }
    )
  }

  constructor(pinia, router, message, modal) {
    this.userStore = useUserStore(pinia)
    this.router = router
    this.queue = {}
    this.message = message
    this.modal = modal
  }
}

export default http
