// 字母下拉组件

<template>
  <div class="letter-pullup-container" ref="containerRef">
    <div
      v-for="(letter, index) in letters"
      :key="`${letter}-${index}`"
      class="letter-wrapper"
    >
      <Motion
        as="span"
        :initial="pullupVariant.initial"
        :animate="isInView ? pullupVariant.animate : pullupVariant.initial"
        :transition="{
          delay: isInView ? index * (props.delay ? props.delay : 0.05) : 0,
          duration: 0.5,
          ease: [0.25, 0.46, 0.45, 0.94]
        }"
        :class="
          cn(
            'letter-animated',
            props.class,
          )
        "
      >
        <span v-if="letter === ' '">&nbsp;</span>
        <span v-else>{{ letter }}</span>
      </Motion>
    </div>
  </div>
</template>

<script setup>
import { Motion } from "motion-v";
import { ref, onMounted, onUnmounted } from 'vue';

// 简单的cn函数实现（如果项目中没有的话）
const cn = (...classes) => {
  return classes.filter(Boolean).join(' ');
};

const props = defineProps({
  class: {
    type: String,
    default: ''
  },
  words: {
    type: String,
    required: true
  },
  delay: {
    type: Number,
    default: 0.05
  },
  threshold: {
    type: Number,
    default: 0.1
  }
});

const letters = props.words.split("");
const containerRef = ref(null);
const isInView = ref(false);
let observer = null;

const pullupVariant = {
  initial: { y: 50, opacity: 0, scale: 0.8 },
  animate: {
    y: 0,
    opacity: 1,
    scale: 1,
  },
};

onMounted(() => {
  if (containerRef.value) {
    observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            isInView.value = true;
            // 一旦触发动画，就停止观察
            observer.unobserve(entry.target);
          }
        });
      },
      {
        threshold: props.threshold,
        rootMargin: '50px 0px -100px 0px'
      }
    );
    observer.observe(containerRef.value);
  }
});

onUnmounted(() => {
  if (observer && containerRef.value) {
    observer.unobserve(containerRef.value);
  }
});
</script>

<style scoped>
.letter-pullup-container {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
  gap: 1px;
}

.letter-wrapper {
  display: inline-block;
  overflow: hidden;
}

.letter-animated {
  display: inline-block;
  font-family: 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Arial', sans-serif;
  color: inherit;
  transform-origin: center bottom;
}

/* 支持中文字符的特殊处理 */
.letter-animated:lang(zh) {
  font-family: 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  letter-spacing: 0.05em;
}
</style>
