import type { Router } from 'vue-router';

const WHITE_LIST = ['/login'];

export function setupRouterGuards(router: Router) {
  router.beforeEach(async (to, _from, next) => {
    const token = localStorage.getItem('token');

    if (token) {
      if (to.path === '/login') {
        next({ path: '/' });
      } else {
        next();
      }
    } else {
      if (WHITE_LIST.includes(to.path)) {
        next();
      } else {
        next({ path: '/login', query: { redirect: to.fullPath } });
      }
    }
  });
}
