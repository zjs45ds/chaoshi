// 跑马灯组件
<template>
  <div class="marquee-container">
    <!-- 第一行滚动内容 - 从右往左 -->
    <div class="marquee-wrapper">
      <div class="marquee marquee-left">
        <span class="marquee-item">薛之谦的声音太有辨识度了，每首歌都让人沉醉！</span>
        <span class="marquee-item">音乐才华横溢，创作能力惊人，谦谦太棒了！</span>
        <span class="marquee-item">无论是慢歌还是快歌，都能完美驾驭，实力唱将！</span>
        <span class="marquee-item">歌词写得真好，每一句都深深打动人心！</span>
        <span class="marquee-item">演唱会现场氛围太棒了，看过一次就无法忘怀！</span>
        <span class="marquee-item">音乐风格多变，总能带来惊喜，永远支持薛之谦！</span>
        <span class="marquee-item">人帅歌甜，才华横溢，这样的歌手哪里找！</span>
        <span class="marquee-item">从《认真的雪》到现在，一路见证你的成长，为你骄傲！</span>
        <!-- 复制内容用于无缝滚动 -->
        <span class="marquee-item">薛之谦的声音太有辨识度了，每首歌都让人沉醉！</span>
        <span class="marquee-item">音乐才华横溢，创作能力惊人，谦谦太棒了！</span>
        <span class="marquee-item">无论是慢歌还是快歌，都能完美驾驭，实力唱将！</span>
        <span class="marquee-item">歌词写得真好，每一句都深深打动人心！</span>
        <span class="marquee-item">演唱会现场氛围太棒了，看过一次就无法忘怀！</span>
        <span class="marquee-item">音乐风格多变，总能带来惊喜，永远支持薛之谦！</span>
        <span class="marquee-item">人帅歌甜，才华横溢，这样的歌手哪里找！</span>
        <span class="marquee-item">从《认真的雪》到现在，一路见证你的成长，为你骄傲！</span>
      </div>
    </div>
    
    <!-- 第二行滚动内容 - 从左往右 -->
    <div class="marquee-wrapper">
      <div class="marquee marquee-right">
        <span class="marquee-item">《演员》这首歌太经典了，百听不厌！</span>
        <span class="marquee-item">薛之谦的创作总能引起共鸣，真的很用心！</span>
        <span class="marquee-item">每首歌都有故事，这样的音乐人不多了！</span>
        <span class="marquee-item">舞台表现力十足，演唱会真的很值得看！</span>
        <span class="marquee-item">声音很有特色，一听就知道是薛之谦！</span>
        <span class="marquee-item">歌曲质量一直很高，从未让人失望！</span>
        <span class="marquee-item">不仅歌唱得好，综艺感也很强，太有才华了！</span>
        <span class="marquee-item">新歌《跃》真的太好听了，循环播放中！</span>
        <span class="marquee-item">从选秀节目出道到现在，一路逆袭太励志了！</span>
        <span class="marquee-item">希望薛之谦能一直创作下去，带来更多好音乐！</span>
        <!-- 复制内容用于无缝滚动 -->
        <span class="marquee-item">《演员》这首歌太经典了，百听不厌！</span>
        <span class="marquee-item">薛之谦的创作总能引起共鸣，真的很用心！</span>
        <span class="marquee-item">每首歌都有故事，这样的音乐人不多了！</span>
        <span class="marquee-item">舞台表现力十足，演唱会真的很值得看！</span>
        <span class="marquee-item">声音很有特色，一听就知道是薛之谦！</span>
        <span class="marquee-item">歌曲质量一直很高，从未让人失望！</span>
        <span class="marquee-item">不仅歌唱得好，综艺感也很强，太有才华了！</span>
        <span class="marquee-item">新歌《跃》真的太好听了，循环播放中！</span>
        <span class="marquee-item">从选秀节目出道到现在，一路逆袭太励志了！</span>
        <span class="marquee-item">希望薛之谦能一直创作下去，带来更多好音乐！</span>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
// 简化组件，使用固定内容
</script> 

<style scoped>
/* 主容器 */
.marquee-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 20px;
}

/* 滚动容器 */
.marquee-wrapper {
  overflow: hidden;
  position: relative;
  width: 100%;
  height: 80px;
}

/* 滚动内容 */
.marquee {
  display: flex;
  white-space: nowrap;
  height: 100%;
  align-items: center;
  animation-timing-function: linear;
  animation-iteration-count: infinite;
}

/* 从右往左滚动 */
.marquee-left {
  animation: marquee-left 30s linear infinite;
}

/* 从左往右滚动 */
.marquee-right {
  animation: marquee-right 45s linear infinite;
}

/* 鼠标悬停时暂停 */
.marquee:hover {
  animation-play-state: paused;
}

/* 动画关键帧 - 从右往左 */
@keyframes marquee-left {
  0% {
    transform: translateX(0);
  }
  100% {
    transform: translateX(-50%);
  }
}

/* 动画关键帧 - 从左往右 */
@keyframes marquee-right {
  0% {
    transform: translateX(-50%);
  }
  100% {
    transform: translateX(0);
  }
}

/* 滚动文本项样式 */
.marquee-item {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.15), rgba(255, 255, 255, 0.1));
  backdrop-filter: blur(12px);
  padding: 12px 24px;
  margin: 0 10px;
  border-radius: 30px;
  border: 1px solid rgba(255, 255, 255, 0.25);
  color: white;
  font-size: 1.1rem;
  font-weight: 500;
  white-space: nowrap;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.25);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

/* 悬停效果 */
.marquee-item:hover {
  transform: translateY(-3px);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.25), rgba(255, 255, 255, 0.15));
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.35);
  border-color: rgba(255, 255, 255, 0.4);
  scale: 1.05;
}
</style>