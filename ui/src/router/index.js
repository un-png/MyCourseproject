import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue')
      },
      {
        path: 'content',
        name: 'Content',
        component: () => import('../views/content/ContentList.vue')
      },
      {
        path: 'content/edit/:id?',
        name: 'ContentEdit',
        component: () => import('../views/content/ContentEdit.vue')
      },
      {
        path: 'content/audit',
        name: 'ContentAudit',
        component: () => import('../views/content/ContentAudit.vue')
      },
      {
        path: 'comments',
        name: 'Comments',
        component: () => import('../views/interaction/Comments.vue')
      },
      {
        path: 'feedback',
        name: 'Feedback',
        component: () => import('../views/interaction/Feedback.vue')
      },
      {
        path: 'advertisement',
        name: 'Advertisement',
        component: () => import('../views/advertisement/AdvertisementList.vue')
      },
      {
        path: 'advertisement/edit/:id?',
        name: 'AdvertisementEdit',
        component: () => import('../views/advertisement/AdvertisementEdit.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router 