<template>
  <svg 
    :class="['play-icon-svg', sizeClass, customClass]" 
    viewBox="0 0 1024 1024" 
    version="1.1" 
    xmlns="http://www.w3.org/2000/svg"
  >
    <path d="M955.733333 512L68.266667 1024V0z" fill="currentColor"></path>
  </svg>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  size: {
    type: String,
    default: 'medium', // small, medium, large, xlarge
    validator: (value) => ['small', 'medium', 'large', 'xlarge'].includes(value)
  },
  customClass: {
    type: String,
    default: ''
  }
})

const sizeClass = computed(() => {
  const sizeMap = {
    small: 'play-icon-svg-small',
    medium: 'play-icon-svg-medium', 
    large: 'play-icon-svg-large',
    xlarge: 'play-icon-svg-xlarge'
  }
  return sizeMap[props.size]
})
</script>

<style scoped>
.play-icon-svg-small {
  width: 14px;
  height: 14px;
}

.play-icon-svg-medium {
  width: 20px;
  height: 20px;
}

.play-icon-svg-large {
  width: 24px;
  height: 24px;
}

.play-icon-svg-xlarge {
  width: 32px;
  height: 32px;
}

.play-icon-svg {
  transition: all 0.3s ease;
  fill: currentColor;
}
</style>
