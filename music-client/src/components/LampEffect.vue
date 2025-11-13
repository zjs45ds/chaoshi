// 灯效组件
<template>
  <div class="lamp-effect-container" :class="$props.class">
    <div class="lamp-background">
      <!-- Left Conic Gradient -->
      <div class="conic-left animate-conic-gradient">
        <div class="mask-bottom"></div>
        <div class="mask-right"></div>
      </div>

      <!-- Right Conic Gradient -->
      <div class="conic-right animate-conic-gradient">
        <div class="mask-bottom-right"></div>
        <div class="mask-left"></div>
      </div>

      <!-- Background elements -->
      <div class="background-blur"></div>
      <div class="background-overlay"></div>
      <div class="center-glow"></div>

      <!-- Spotlight -->
      <div class="spotlight animate-spotlight"></div>

      <!-- Glowing Line -->
      <div class="glowing-line animate-glowing-line"></div>

      <!-- Top mask -->
      <div class="top-mask"></div>
    </div>

    <div class="lamp-content">
      <slot />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, type HTMLAttributes } from "vue";

interface LampEffectProps {
  delay?: number;
  duration?: number;
  class?: HTMLAttributes["class"];
}

const props = withDefaults(defineProps<LampEffectProps>(), {
  delay: 0.5,
  duration: 0.8,
});

const durationInSeconds = computed(() => `${props.duration}s`);
const delayInSeconds = computed(() => `${props.delay}s`);
</script>

<style scoped>
/* Main Container */
.lamp-effect-container {
  position: relative;
  display: flex;
  min-height: 100vh;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background: #020617; /* slate-950 */
  width: 100%;
  border-radius: 6px;
  z-index: 0;
}

/* Background Container */
.lamp-background {
  position: relative;
  isolation: isolate;
  z-index: 0;
  display: flex;
  width: 100%;
  flex: 1;
  transform: scaleY(1.25);
  align-items: center;
  justify-content: center;
}

/* Left Conic Gradient */
.conic-left {
  background: conic-gradient(from 70deg at center top, #06b6d4, transparent, transparent);
  position: absolute;
  right: 50%;
  height: 14rem; /* h-56 */
  width: 15rem; /* w-60 */
  overflow: visible;
  color: white;
  opacity: 0.5;
}

/* Right Conic Gradient */
.conic-right {
  background: conic-gradient(from 290deg at center top, transparent, transparent, #06b6d4);
  position: absolute;
  left: 50%;
  height: 14rem; /* h-56 */
  width: 15rem; /* w-60 */
  color: white;
  opacity: 0.5;
}

/* Masks for Left Gradient */
.mask-bottom {
  position: absolute;
  bottom: 0;
  left: 0;
  z-index: 20;
  height: 10rem; /* h-40 */
  width: 100%;
  background: #020617;
  -webkit-mask-image: linear-gradient(to top, white, transparent);
  mask-image: linear-gradient(to top, white, transparent);
}

.mask-right {
  position: absolute;
  bottom: 0;
  left: 0;
  z-index: 20;
  height: 100%;
  width: 10rem; /* w-40 */
  background: #020617;
  -webkit-mask-image: linear-gradient(to right, white, transparent);
  mask-image: linear-gradient(to right, white, transparent);
}

/* Masks for Right Gradient */
.mask-bottom-right {
  position: absolute;
  bottom: 0;
  right: 0;
  z-index: 20;
  height: 10rem; /* h-40 */
  width: 100%;
  background: #020617;
  -webkit-mask-image: linear-gradient(to top, white, transparent);
  mask-image: linear-gradient(to top, white, transparent);
}

.mask-left {
  position: absolute;
  bottom: 0;
  right: 0;
  z-index: 20;
  height: 100%;
  width: 10rem; /* w-40 */
  background: #020617;
  -webkit-mask-image: linear-gradient(to left, white, transparent);
  mask-image: linear-gradient(to left, white, transparent);
}

/* Background Elements */
.background-blur {
  position: absolute;
  top: 50%;
  height: 12rem; /* h-48 */
  width: 100%;
  transform: translateY(3rem) scaleX(1.5); /* translate-y-12 scale-x-150 */
  background: #020617;
  filter: blur(2rem); /* blur-2xl */
}

.background-overlay {
  position: absolute;
  top: 50%;
  z-index: 50;
  height: 12rem; /* h-48 */
  width: 100%;
  background: transparent;
  opacity: 0.1;
  backdrop-filter: blur(12px); /* backdrop-blur-md */
}

.center-glow {
  position: absolute;
  z-index: 50;
  height: 9rem; /* h-36 */
  width: 28rem; /* w-[28rem] */
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%); /* center positioning */
  border-radius: 50%;
  background: #06b6d4; /* bg-cyan-500 */
  opacity: 0.5;
  filter: blur(3rem); /* blur-3xl */
}

/* Spotlight */
.spotlight {
  position: absolute;
  z-index: 30;
  height: 9rem; /* h-36 */
  width: 8rem; /* w-32 */
  top: 50%;
  left: 50%;
  transform: translate(-50%, -6rem); /* center and move up */
  border-radius: 50%;
  background: #22d3ee; /* bg-cyan-400 */
  filter: blur(2rem); /* blur-2xl */
}

/* Glowing Line */
.glowing-line {
  position: absolute;
  z-index: 50;
  height: 0.125rem; /* h-0.5 */
  width: 15rem; /* w-60 */
  top: 50%;
  left: 50%;
  transform: translate(-50%, -7rem); /* center and move up */
  background: #22d3ee; /* bg-cyan-400 */
}

/* Top Mask */
.top-mask {
  position: absolute;
  z-index: 40;
  height: 11rem; /* h-44 */
  width: 100%;
  top: 50%;
  transform: translateY(-12.5rem);
  background: #020617;
}

/* Content */
.lamp-content {
  position: relative;
  z-index: 50;
  display: flex;
  transform: translateY(-20rem); /* -translate-y-80 */
  flex-direction: column;
  align-items: center;
  padding: 0 1.25rem; /* px-5 */
  text-align: center;
}

/* Animations */
.animate-spotlight {
  animation: spotlight-anim ease-in-out v-bind(durationInSeconds) forwards;
  animation-delay: v-bind(delayInSeconds);
}

.animate-glowing-line {
  animation: glowing-line-anim ease-in-out v-bind(durationInSeconds) forwards;
  animation-delay: v-bind(delayInSeconds);
}

.animate-conic-gradient {
  animation: conic-gradient-anim ease-in-out v-bind(durationInSeconds) forwards;
  animation-delay: v-bind(delayInSeconds);
}

/* Keyframes */
@keyframes spotlight-anim {
  from {
    width: 8rem;
  }
  to {
    width: 16rem;
  }
}

@keyframes glowing-line-anim {
  from {
    width: 15rem;
  }
  to {
    width: 30rem;
  }
}

@keyframes conic-gradient-anim {
  from {
    opacity: 0.5;
    width: 15rem;
  }
  to {
    opacity: 1;
    width: 30rem;
  }
}
</style>