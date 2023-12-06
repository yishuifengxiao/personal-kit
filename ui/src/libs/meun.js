export default function meun(route, routes) {
    const rs = routes.filter(
        (v) =>
        typeof v.children != 'undefined' &&
        v.children.filter((s) => s.name === route.name || s.path === route.path).length > 0
    )
    if (rs.length == 0) {
        return []
    }
    const current = rs[0]

    const meus = current.children.map((v) => {
        return {
            name: v.name,
            label: v.meta.label
        }
    })

    return meus
}