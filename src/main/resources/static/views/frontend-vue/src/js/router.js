import { createWebHistory, createRouter } from 'vue-router'

import Home from '../components/Home.vue'
import PrivacyPolicy from '../components/PrivacyPolicy.vue'
import TermsOfService from '../components/TermsOfService.vue'
import SubtitleConverters from '../components/SubtitleConverters.vue'
import ToSrtConverter from '@/components/ToSrtConverter.vue'
import ToVttConverter from '@/components/ToVttConverter.vue'
import ToAssConverter from '@/components/ToAssConverter.vue'
import SubtitleTranslations from '@/components/SubtitleTranslations.vue'
import FixSubtitleEncodingComponent from '@/components/seo/FixSubtitleEncodingComponent.vue'
import SubtitleFixerComponent from '@/components/seo/SubtitleFixerComponent.vue'
import SrtConverterOnlineComponent from '@/components/seo/SrtConverterOnlineComponent.vue'
import SubtitleEditorOnlineComponent from '@/components/seo/SubtitleEditorOnlineComponent.vue'
import SubtitleShifterOnlineComponent from '@/components/seo/SubtitleShifterOnlineComponent.vue'
import AiSubtitleFixerComponent from '@/components/seo/AiSubtitleFixerComponent.vue'

const routes = [
    {
        path: '/',
        component: Home,
        meta: {
            title: 'Subtitles Corrector - fix subtitle problems',
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
        {
        path: '/translate-subtitles',
        component: SubtitleTranslations,
        meta: {
            title: 'Subtitles Translator',
            description: 'Translate subtitle file to another language.'
        }
    },
        {
        path: '/fix-subtitle-encoding',
        component: FixSubtitleEncodingComponent,
        meta: {
            title: 'Fix Subtitle Encoding Issues (SRT, UTF-8, Broken Characters)',
            description: 'Subtitles showing weird characters like Å½ or Ä‡? Fix encoding issues instantly. Convert SRT files to UTF-8 and repair broken subtitles online.'
        }
    },
        {
        path: '/subtitle-fixer',
        component: SubtitleFixerComponent,
        meta: {
            title: 'Subtitle Fixer - Repair Broken Subtitles Online (Free Tool)',
            description: 'Fix subtitle files with one click. Repair encoding, sync timing, remove errors, and clean subtitles online for free.'
        }
    },
        {
        path: '/srt-converter-online',
        component: SrtConverterOnlineComponent,
        meta: {
            title: 'SRT Converter Online - Convert Subtitles to SRT Format Free',
            description: 'Convert subtitles to SRT format instantly. Supports multiple formats, fast and free. No installation needed.'
        }
    },
        {
        path: '/subtitle-editor-online',
        component: SubtitleEditorOnlineComponent,
        meta: {
            title: 'Subtitle Editor Online - Edit and Fix Subtitles Easily',
            description: 'Edit your subtitle files directly in your browser with a simple and powerful online subtitle editor'
        }
    },
        {
        path: '/subtitle-shifter-online',
        component: SubtitleShifterOnlineComponent,
        meta: {
            title: 'Subtitle Shifter Online - Sync Subtitles Forward or Backward',
            description: 'Easily shift subtitles forward or backward to match your video timing. Fix subtitle delays in seconds using this simple online subtitle sync tool.'
        }
    },
    {
        path: '/ai-subtitle-fixer',
        component: AiSubtitleFixerComponent,
        meta: {
            title: 'AI Subtitle Fixer Online - Fix Grammar & Improve Readability',
            description: 'Automatically fix subtitle grammar, spelling, and readability using AI Upload your subtitle file and let the tool analyze and improve each line in seconds.'
        }
    }
    
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