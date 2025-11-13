<!-- 翻转卡片组件 -->
<template>
  <div class="flip-card-container" :class="props.class">
    <div class="flip-card-inner">
      <!-- Front -->
      <div class="flip-card-front">
        <slot />
      </div>

      <!-- Back -->
      <div class="flip-card-back">
        <slot name="back" />
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  rotate: {
    type: String,
    default: "y",
    validator: (value) => ["x", "y"].includes(value)
  },
  class: String
});
</script>

<style scoped>
.flip-card-container {
  width: 100%;
  height: 280px;
  perspective: 1000px;
  background-color: transparent;
}

.flip-card-inner {
  position: relative;
  width: 100%;
  height: 100%;
  text-align: center;
  transition: transform 0.8s ease;
  transform-style: preserve-3d;
}

.flip-card-container:hover .flip-card-inner {
  transform: rotateY(180deg);
}

.flip-card-front, 
.flip-card-back {
  position: absolute;
  width: 100%;
  height: 100%;
  backface-visibility: hidden;
  -webkit-backface-visibility: hidden;
  -moz-backface-visibility: hidden;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.flip-card-front {
  background-color: #f0f0f0;
  z-index: 2;
}

.flip-card-back {
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.9), rgba(0, 0, 0, 0.8));
  color: white;
  transform: rotateY(180deg);
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  box-sizing: border-box;
}
</style>