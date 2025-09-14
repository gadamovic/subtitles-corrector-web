import { createWebHistory, createRouter } from 'vue-router'

import Home from '../components/Home.vue'
import PrivacyPolicy from '../components/PrivacyPolicy.vue'
import TermsOfService from '../components/TermsOfService.vue'
import SubtitleConverters from '../components/SubtitleConverters.vue'
import ToSrtConverter from '@/components/ToSrtConverter.vue'
import ToVttConverter from '@/components/ToVttConverter.vue'
import ToAssConverter from '@/components/ToAssConverter.vue'

const routes = [
    {
        path: '/',
        component: Home,
        meta: {
            title: 'Subtitles Corrector',
            //use default description
        }

    },
    {
        path: '/privacy-policy',
        component: PrivacyPolicy,
        meta: {
            title: 'Privacy Policy',
            description: 'What we do with the data you provide to us'
        }
    },
    {
        path: '/terms-of-service',
        component: TermsOfService,
        meta: {
            title: 'Terms of Service',
            description: 'Legal agreements that you comply with by using our app'
        }
    },
    {
        path: '/converters',
        component: SubtitleConverters,
        meta: {
            title: 'Subtitle Converters',
            description: 'Convert between various subtitle file formats'
        }
    },
    {
        path: '/to-srt-converter',
        component: ToSrtConverter,
        meta: {
            title: 'Srt converter',
            description: 'Convert any subtitle format to srt. vtt to srt converter, ass to srt converter.'
        }
    },
    {
        path: '/to-vtt-converter',
        component: ToVttConverter,
        meta: {
            title: 'Vtt converter',
            description: 'Convert any subtitle format to vtt. srt to vtt converter, ass to vtt converter.'
        }
    },
    {
        path: '/to-ass-converter',
        component: ToAssConverter,
        meta: {
            title: 'Ass converter',
            description: 'Convert any subtitle format to ass. srt to ass converter, ass to ass converter.'
        }
    },
]

export const router = createRouter({
    history: createWebHistory(),
    routes,
})

router.beforeEach((to) => {
    document.title = to.meta?.title ?? 'Subtitles Corrector'

    const description = to.meta?.description ?? "An AI-powered app that easily fixes character encoding, formatting and synchronization issues, ensuring your subtitle files are clean, accurate, and ready for use."
    const descriptionElement = document.querySelector('head meta[name="description"]')
    descriptionElement.setAttribute('content', description || description)
})