---
name: PixelFlow
colors:
  surface: '#f9f9f9'
  surface-dim: '#dadada'
  surface-bright: '#f9f9f9'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f3f4'
  surface-container: '#eeeeee'
  surface-container-high: '#e8e8e8'
  surface-container-highest: '#e2e2e2'
  on-surface: '#1a1c1c'
  on-surface-variant: '#3e4941'
  inverse-surface: '#2f3131'
  inverse-on-surface: '#f0f1f1'
  outline: '#6e7a70'
  outline-variant: '#becabf'
  surface-tint: '#006d41'
  primary: '#006d41'
  on-primary: '#ffffff'
  primary-container: '#6fcf97'
  on-primary-container: '#005733'
  inverse-primary: '#7adaa1'
  secondary: '#006d37'
  on-secondary: '#ffffff'
  secondary-container: '#7bf8a1'
  on-secondary-container: '#007239'
  tertiary: '#4e6358'
  on-tertiary: '#ffffff'
  tertiary-container: '#abc2b4'
  on-tertiary-container: '#3b5045'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#95f7bb'
  primary-fixed-dim: '#7adaa1'
  on-primary-fixed: '#002110'
  on-primary-fixed-variant: '#005230'
  secondary-fixed: '#7efba4'
  secondary-fixed-dim: '#61de8a'
  on-secondary-fixed: '#00210c'
  on-secondary-fixed-variant: '#005228'
  tertiary-fixed: '#d0e8d9'
  tertiary-fixed-dim: '#b5ccbe'
  on-tertiary-fixed: '#0b1f16'
  on-tertiary-fixed-variant: '#374b41'
  background: '#f9f9f9'
  on-background: '#1a1c1c'
  surface-variant: '#e2e2e2'
typography:
  display-lg:
    fontFamily: Manrope
    fontSize: 40px
    fontWeight: '800'
    lineHeight: 48px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Manrope
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.01em
  headline-lg-mobile:
    fontFamily: Manrope
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 36px
  headline-md:
    fontFamily: Manrope
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  body-lg:
    fontFamily: Manrope
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Manrope
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-lg:
    fontFamily: Manrope
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.01em
  label-sm:
    fontFamily: Manrope
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 4px
  xs: 4px
  sm: 8px
  md: 16px
  lg: 24px
  xl: 32px
  gutter: 16px
  margin-mobile: 20px
  margin-tablet: 32px
---

## Brand & Style
The design system is engineered for a premium, utility-focused mobile experience. It merges the structured hierarchy of Material 3 with the airy, high-end finish of Apple’s design language. The brand personality is "Efficient Elegance"—it feels like a professional tool that is paradoxically simple to use.

The aesthetic follows a **Minimalist** approach with subtle **Glassmorphic** accents. It prioritizes clarity, utilizing generous whitespace to reduce cognitive load during image processing tasks. Every interaction should feel intentional and snappy, reinforcing the "privacy-first" and "fast" brand promises through a lack of decorative clutter and a focus on high-quality functional elements.

## Colors
This design system utilizes a monochrome-adjacent palette anchored by refreshing greens. 
- **Primary Light Green (#6FCF97)** is used for primary actions and active states.
- **Secondary Emerald (#27AE60)** is reserved for high-emphasis call-to-actions and success states.
- **Mint Green (#DFF7E8)** serves as a soft background for chips, light alerts, or container fills to provide subtle tonal variance without introducing heavy color.
- **Neutral (#FFFFFF)** is the foundational background color to ensure the UI feels expansive and clean.

A signature **Soft Gradient** (Primary to Secondary) is applied sparingly to primary action buttons and progress indicators to create a sense of depth and premium quality.

## Typography
The typography system uses **Manrope** for its modern, geometric construction and excellent legibility in technical contexts. The scale is intentionally large to facilitate quick scanning on mobile devices. 

Headlines use semi-bold and bold weights with tighter letter-spacing to create a "locked-in," professional look. Body text maintains a generous line height to enhance readability against the white background. For mobile-specific optimization, top-level headings scale down slightly to ensure they don't wrap awkwardly on smaller widths while retaining their visual impact.

## Layout & Spacing
The design system employs a **Fluid Grid** model with a base-4 spacing scale. On mobile, a 20px side margin is maintained to create breathing room, while internal gutters are set to 16px. 

The spacing philosophy is "Airy Utility"—preferring larger margins (`lg` and `xl`) between distinct functional groups to reinforce the minimalist vibe. Content is organized in vertical stacks where padding within cards is consistently 16px or 20px to ensure touch targets remain large and accessible.

## Elevation & Depth
Depth is communicated through **Ambient Shadows** and **Tonal Layering** rather than heavy borders. 
- **Surface 0 (Background):** Pure #FFFFFF.
- **Surface 1 (Cards/Modals):** Pure #FFFFFF but elevated by a soft, diffused shadow (0px 8px 24px rgba(0, 0, 0, 0.04)).
- **Interactive Elements:** Use a slightly tighter, more saturated shadow when pressed to simulate physical tactility.

Backdrop blurs (20px-30px) are used for navigation bars and sticky headers to maintain context of the content scrolling underneath, creating a premium "glass" feel characteristic of modern mobile OS standards.

## Shapes
The shape language is friendly and modern. A consistent **Rounded (0.5rem base)** logic is applied, but container-level elements like cards and image previews use `rounded-xl` (1.5rem/24px) to emphasize the soft, premium aesthetic. Buttons use a pill-shape or a high-radius corner (16px+) to ensure they feel comfortable for thumb interaction.

## Components
- **Buttons:** Primary buttons feature the light-to-emerald gradient with white text. Secondary buttons use the Mint Green fill with Emerald text. All buttons have a minimum height of 56px for optimal touch accessibility.
- **Cards:** Cards should have no borders. They rely on the soft 4% opacity ambient shadow for definition.
- **Inputs:** Search and text fields use a subtle #F9FAFB fill with a 1px #E5E7EB border that turns Emerald Green on focus.
- **Icons:** Use thin-stroke, modern outlined icons. Key icons should feature a two-tone style where a small portion of the icon (like a "plus" or a "sparkle") is highlighted in Emerald Green.
- **Chips:** Highly rounded (pill) with Mint Green backgrounds and Emerald Green labels, used for image tags or filter states.
- **Image Previews:** Always rendered with a 16px corner radius and a 1px inner stroke of 5% black to define edges against the white background.