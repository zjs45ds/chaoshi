// 游戏页面
<template>
  <div class="game-container">
    <h1 class="game-title">谦谦消消乐</h1>
    <div class="game-info">
      <div class="level">关卡: {{ level }}</div>
      <div class="score">得分: {{ score }} / {{ targetScore }}</div>
      <div class="steps">步数: {{ steps }} / {{ maxSteps }}</div>
    </div>
    <div class="game-board">
      <!-- 游戏棋盘将在这里渲染 -->
      <div v-for="(row, rowIndex) in gameBoard" :key="rowIndex" class="game-row">
        <div v-for="(cell, colIndex) in row" :key="colIndex" class="game-cell">
          <div
            :class="['game-item', 'item-' + cell.type, { 'is-selected': cell.isSelected, 'is-animating': cell.isAnimating }]"
            :style="{ backgroundColor: getColor(cell.type) }"
            @click="handleCellClick(rowIndex, colIndex)"
          >
            <img :src="getIcon(cell.type)" class="game-icon" alt="game icon">
          </div>
        </div>
      </div>
    </div>
    <div class="game-controls">
      <button class="restart-btn" @click="initGame">重新开始</button>
      <button class="shuffle-btn" @click="shuffleBoard" :disabled="gameOver || isPaused || hasPossibleMatches()">洗牌</button>
      <button v-if="!gameOver && !isPaused" @click="pauseGame" class="pause-btn">暂停游戏</button>
      <button v-if="!gameOver && isPaused" @click="resumeGame" class="resume-btn">继续游戏</button>
    </div>

    <!-- 游戏暂停提示 -->
    <div v-if="isPaused" class="pause-overlay">
      <div class="pause-message">
        <h2>游戏暂停</h2>
        <p>点击"继续游戏"按钮恢复</p>
      </div>
    </div>

    <!-- 关卡升级对话框 -->
    <div class="dialog-overlay" v-if="showLevelUpDialog">
      <div class="dialog">
        <h2>关卡完成!</h2>
        <p>恭喜你完成了第 {{ level }} 关!</p>
        <p>得分: {{ score }} / {{ targetScore }}</p>
        <div class="dialog-buttons">
          <button @click="continueToNextLevel">继续下一关</button>
          <button @click="playCurrentLevelAgain">再玩一次</button>
        </div>
      </div>
    </div>

    <!-- 游戏结束界面 -->
    <div class="game-over" v-if="gameOver">
      <h2>游戏结束!</h2>
      <p>你的得分: {{ score }}</p>
      <button @click="restartGame">再玩一次</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

// 导入图片资源
import image1 from '../assets/image1.png'
import image2 from '../assets/image2.png'
import image3 from '../assets/image3.png'
import image4 from '../assets/image4.png'
import image5 from '../assets/image5.png'

// 游戏配置
const ROWS = 6
const COLS = 6
const ITEM_TYPES = 5 // 不同类型的图标数量

// 无限关卡配置 - 动态计算目标分数和最大步数
// 基础值
const BASE_TARGET_SCORE = 717; // 第一关需要717分
const BASE_MAX_STEPS = 20; // 第一关开始步数为20步
// 增量
const TARGET_SCORE_INCREMENT = 170; // 后续关卡每关增加170分
const MAX_STEPS_INCREMENT = 7;    // 后续关卡每关增加7步  
// 最大步数最小值（初始关卡的最小值）
const MIN_MAX_STEPS = 10;

// 游戏状态
const gameBoard = ref([])
const score = ref(0)
const selectedCell = ref(null)
const gameOver = ref(false)
const isPaused = ref(false) // 游戏暂停状态
const level = ref(1)        // 当前关卡
const steps = ref(0)        // 已用步数
const maxSteps = ref(BASE_MAX_STEPS) // 最大步数
const targetScore = ref(BASE_TARGET_SCORE) // 目标分数
const showLevelUpDialog = ref(false) // 是否显示关卡升级对话框

// 初始化游戏
function initGame() {
  const board = []
  for (let i = 0; i < ROWS; i++) {
    const row = []
    for (let j = 0; j < COLS; j++) {
      // 确保不会生成三个连续相同的元素
      let type
      do {
        type = Math.floor(Math.random() * ITEM_TYPES) + 1
      } while (
        // 检查水平方向
        (j >= 2 && row[j-1].type === type && row[j-2].type === type) ||
        // 检查垂直方向
        (i >= 2 && board[i-1][j].type === type && board[i-2][j].type === type)
      )
      row.push({ type, isMatched: false, isSelected: false, isAnimating: false })
    }
    board.push(row)
  }
  gameBoard.value = board
  score.value = 0
  steps.value = 0
  gameOver.value = false
  isPaused.value = false
  // 重置关卡
  if (gameOver.value) {
    level.value = 1
    maxSteps.value = BASE_MAX_STEPS
    targetScore.value = BASE_TARGET_SCORE
  }
}

// 暂停游戏
function pauseGame() {
  if (!gameOver.value && !isPaused.value) {
    isPaused.value = true
  }
}

// 继续游戏
function resumeGame() {
  if (!gameOver.value && isPaused.value) {
    isPaused.value = false
  }
}

// 获取图标的颜色
function getColor(type) {
  const colors = ['#ff6b6b', '#4ecdc4', '#45b7d1', '#ffd166', '#6a0572']
  return colors[type - 1] || '#ccc'
}

// 获取图标的图片路径
function getIcon(type) {
  // 图片数组，索引从1开始对应type值
  const images = [null, image1, image2, image3, image4, image5]
  return images[type]
}

// 处理单元格点击
function handleCellClick(row, col) {
  // 如果游戏结束、暂停或单元格正在动画中，不响应点击
  if (gameOver.value || isPaused.value || gameBoard.value[row][col].isAnimating) {
    return
  }

  if (selectedCell.value === null) {
    // 第一次选择
    selectedCell.value = { row, col }
    gameBoard.value[row][col].isSelected = true
  } else {
    // 第二次选择
    const { row: selectedRow, col: selectedCol } = selectedCell.value
    
    // 检查是否是同一个单元格
    if (row === selectedRow && col === selectedCol) {
      // 取消选择
      gameBoard.value[row][col].isSelected = false
      selectedCell.value = null
      return
    }
    
    // 检查是否相邻
    if (
      (Math.abs(row - selectedRow) === 1 && col === selectedCol) ||
      (Math.abs(col - selectedCol) === 1 && row === selectedRow)
    ) {
      // 取消之前的选择样式
      gameBoard.value[selectedRow][selectedCol].isSelected = false
      
      // 交换元素
      swapCells(selectedRow, selectedCol, row, col)
      
      // 增加步数
      steps.value++
      
      // 检查是否达到步数上限
      if (steps.value >= maxSteps.value) {
        gameOver.value = true
        return
      }
      
      // 检查是否有匹配
      const hasMatch = checkMatches()
      
      if (!hasMatch) {
  // 如果没有匹配，交换回来 - 进一步优化延迟时间
  setTimeout(() => {
    swapCells(row, col, selectedRow, selectedCol)
  }, 150)
} else {
  // 如果有匹配，添加动画效果并移除匹配的元素 - 保持优化的延迟时间
  setTimeout(() => {
          removeMatchesWithAnimation()
        }, 50)
}
    }
    
    // 取消选择
    selectedCell.value = null
  }
}

// 交换两个单元格
function swapCells(row1, col1, row2, col2) {
  // 检查单元格是否存在
  if (
    gameBoard.value[row1] && 
    gameBoard.value[row1][col1] && 
    gameBoard.value[row2] && 
    gameBoard.value[row2][col2]
  ) {
    const temp = gameBoard.value[row1][col1];
    gameBoard.value[row1][col1] = gameBoard.value[row2][col2];
    gameBoard.value[row2][col2] = temp;
  }
}

// 检查是否有匹配 (3个或以上相同类型元素)
function checkMatches() {
  let hasMatch = false
  
  // 检查水平方向匹配
  for (let i = 0; i < ROWS; i++) {
    let currentType = gameBoard.value[i][0].type // 当前行的第一个元素类型
    let count = 1 // 记录连续相同元素的数量
    
    for (let j = 1; j < COLS; j++) {
      // 如果当前元素类型与前一个相同且不为空
      if (gameBoard.value[i][j].type === currentType && currentType !== 0) {
        count++ // 增加连续计数
      } else {
        // 遇到不同类型元素，检查之前的连续元素是否达到3个或更多
        if (count >= 3) {
          // 标记这一段连续元素为匹配状态
          for (let k = j - count; k < j; k++) {
            gameBoard.value[i][k].isMatched = true
            hasMatch = true
          }
        }
        // 重置类型和计数
        currentType = gameBoard.value[i][j].type
        count = 1
      }
    }
    
    // 检查行末尾的连续元素是否达到3个或更多
    if (count >= 3) {
      for (let k = COLS - count; k < COLS; k++) {
        gameBoard.value[i][k].isMatched = true
        hasMatch = true
      }
    }
  }
  
  // 检查垂直方向匹配
  for (let j = 0; j < COLS; j++) {
    let currentType = gameBoard.value[0][j].type // 当前列的第一个元素类型
    let count = 1 // 记录连续相同元素的数量
    
    for (let i = 1; i < ROWS; i++) {
      // 如果当前元素类型与上一个相同且不为空
      if (gameBoard.value[i][j].type === currentType && currentType !== 0) {
        count++ // 增加连续计数
      } else {
        // 遇到不同类型元素，检查之前的连续元素是否达到3个或更多
        if (count >= 3) {
          // 标记这一段连续元素为匹配状态
          for (let k = i - count; k < i; k++) {
            gameBoard.value[k][j].isMatched = true
            hasMatch = true
          }
        }
        // 重置类型和计数
        currentType = gameBoard.value[i][j].type
        count = 1
      }
    }
    
    // 检查列末尾的连续元素是否达到3个或更多
    if (count >= 3) {
      for (let k = ROWS - count; k < ROWS; k++) {
        gameBoard.value[k][j].isMatched = true
        hasMatch = true
      }
    }
  }
  
  return hasMatch
}

// 移除匹配的元素（带动画）
function removeMatchesWithAnimation() {
  let matches = 0
  // 标记要移除的元素
  for (let i = 0; i < ROWS; i++) {
    for (let j = 0; j < COLS; j++) {
      if (gameBoard.value[i][j].isMatched) {
        gameBoard.value[i][j].isAnimating = true
        matches++
      }
    }
  }

  // 移除匹配音效代码

  // 更新得分
  score.value += matches * 10

  // 检查是否达到目标分数，显示升级对话框
if (score.value >= targetScore.value) {
  // 显示关卡升级对话框
  showLevelUpDialog.value = true;
}
  // 动画结束后移除元素
  setTimeout(() => {
    for (let i = 0; i < ROWS; i++) {
      for (let j = 0; j < COLS; j++) {
        if (gameBoard.value[i][j].isAnimating) {
          gameBoard.value[i][j] = { type: 0, isMatched: false, isSelected: false, isAnimating: false }
        }
        // 重置所有匹配标记
        gameBoard.value[i][j].isMatched = false
      }
    }
    
    // 移除后立即填充
    fillEmptyCells()
    
    // 清除缓存，因为游戏板已更改
    lastBoardHash.value = '';
    possibleMatchesCache.value = null;
    
    // 检查是否有新的匹配 - 掉落后不要太快检查新匹配
    setTimeout(() => {
      checkAndRemoveMatches()
    }, 300) // 增加延迟时间，使掉落更自然
  }, 250) // 保持动画延迟时间
}

// 已在上方定义缓存变量，此处移除重复定义


// 继续到下一关卡
function continueToNextLevel() {
  showLevelUpDialog.value = false;
  level.value++;
  // 重置分数
  score.value = 0;
  // 动态计算下一关的最大步数（随关卡增加而增加）
  maxSteps.value = BASE_MAX_STEPS + (level.value - 1) * MAX_STEPS_INCREMENT;
  // 重置步数为当前关卡的最大步数
  steps.value = maxSteps.value;
  // 动态计算下一关的目标分数
  targetScore.value = BASE_TARGET_SCORE + (level.value - 1) * TARGET_SCORE_INCREMENT;
}

// 再玩一次当前关卡
function playCurrentLevelAgain() {
  showLevelUpDialog.value = false;
  // 重置游戏板、分数和步数，但保持当前关卡
  initGame();
  // 恢复当前关卡设置
  targetScore.value = BASE_TARGET_SCORE + (level.value - 1) * TARGET_SCORE_INCREMENT;
  maxSteps.value = BASE_MAX_STEPS + (level.value - 1) * MAX_STEPS_INCREMENT;
}

// 连续消除函数
function checkAndRemoveMatches() {
  if (checkMatches()) {
    removeMatchesWithAnimation()
  }
}

// 移除匹配的元素（旧版本，保留用于兼容）
function removeMatches() {
  let matches = 0
  for (let i = 0; i < ROWS; i++) {
    for (let j = 0; j < COLS; j++) {
      if (gameBoard.value[i][j].isMatched) {
        gameBoard.value[i][j] = { type: 0, isMatched: false, isSelected: false, isAnimating: false }
        matches++
      }
    }
  }
  
  // 更新得分
  score.value += matches * 10
}

// 填充空白单元格
function fillEmptyCells() {
  // 对每一列进行处理
  for (let j = 0; j < COLS; j++) {
    // 下落：将非空元素向下移动
    for (let i = ROWS - 1; i >= 0; i--) {
      if (gameBoard.value[i][j].type === 0) {
        // 查找上方最近的非空单元格
        for (let k = i - 1; k >= 0; k--) {
          if (gameBoard.value[k][j].type !== 0) {
            // 下落
            gameBoard.value[i][j] = { ...gameBoard.value[k][j] }
            gameBoard.value[k][j] = { type: 0, isMatched: false, isSelected: false, isAnimating: false }
            break
          }
        }
      }
    }
    
    // 顶部填充新元素
    for (let i = 0; i < ROWS; i++) {
      if (gameBoard.value[i][j].type === 0) {
        // 确保生成的类型在有效范围内
        const newType = Math.floor(Math.random() * ITEM_TYPES) + 1
        gameBoard.value[i][j] = { type: newType, isMatched: false, isSelected: false, isAnimating: false }
      }
    }
  }
}

// 重新开始游戏
function restartGame() {
  initGame()
}

// 缓存可能的匹配结果，避免频繁计算
const possibleMatchesCache = ref(null);
const lastBoardHash = ref('');

// 生成游戏板的简单哈希值，用于检测变化
function generateBoardHash() {
  let hash = '';
  for (let i = 0; i < ROWS; i++) {
    if (!gameBoard.value[i]) continue; // 检查行是否存在
    for (let j = 0; j < COLS; j++) {
      if (!gameBoard.value[i][j]) continue; // 检查单元格是否存在
      hash += gameBoard.value[i][j].type;
    }
  }
  return hash;
}

// 检查是否有可能的匹配
function hasPossibleMatches() {
  const currentHash = generateBoardHash();
  
  // 如果游戏板没有变化，返回缓存的结果
  if (currentHash === lastBoardHash.value && possibleMatchesCache.value !== null) {
    return possibleMatchesCache.value;
  }

  // 检查所有相邻的单元格，看是否有潜在的匹配
  let hasPossible = false;
  for (let i = 0; i < ROWS && !hasPossible; i++) {
    if (!gameBoard.value[i]) continue; // 检查行是否存在
    
    for (let j = 0; j < COLS && !hasPossible; j++) {
      if (!gameBoard.value[i][j]) continue; // 检查单元格是否存在
      
      // 当前单元格类型
      const currentType = gameBoard.value[i][j].type;
      if (currentType === 0) continue; // 跳过空单元格

      // 检查与右侧单元格交换是否会产生匹配
      if (j < COLS - 1 && gameBoard.value[i][j+1]) {
        // 交换
        swapCells(i, j, i, j + 1);
        // 检查匹配
        const hasMatch = checkMatches();
        // 交换回来
        swapCells(i, j + 1, i, j);
        // 重置匹配标记
        resetMatchFlags();
        if (hasMatch) {
          hasPossible = true;
          break;
        }
      }

      // 检查与下方单元格交换是否会产生匹配
      if (i < ROWS - 1 && gameBoard.value[i+1] && gameBoard.value[i+1][j] && !hasPossible) {
        // 交换
        swapCells(i, j, i + 1, j);
        // 检查匹配
        const hasMatch = checkMatches();
        // 交换回来
        swapCells(i + 1, j, i, j);
        // 重置匹配标记
        resetMatchFlags();
        if (hasMatch) {
          hasPossible = true;
          break;
        }
      }
    }
  }

  // 更新缓存
  lastBoardHash.value = currentHash;
  possibleMatchesCache.value = hasPossible;
  
  return hasPossible;
}

// 重置所有匹配标记
function resetMatchFlags() {
  for (let i = 0; i < ROWS; i++) {
    for (let j = 0; j < COLS; j++) {
      gameBoard.value[i][j].isMatched = false;
    }
  }
}

// 洗牌功能
function shuffleBoard() {
  // 记录当前游戏板的非空元素
  const nonEmptyCells = []
  for (let i = 0; i < ROWS; i++) {
    for (let j = 0; j < COLS; j++) {
      if (gameBoard.value[i][j].type !== 0) {
        nonEmptyCells.push(gameBoard.value[i][j].type)
      }
    }
  }
  
  // 打乱数组
  for (let i = nonEmptyCells.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1))
    [nonEmptyCells[i], nonEmptyCells[j]] = [nonEmptyCells[j], nonEmptyCells[i]]
  }
  
  // 重新填充游戏板
  let index = 0
  for (let i = 0; i < ROWS; i++) {
    for (let j = 0; j < COLS; j++) {
      if (gameBoard.value[i][j].type !== 0) {
        gameBoard.value[i][j].type = nonEmptyCells[index++]
        gameBoard.value[i][j].isMatched = false
        gameBoard.value[i][j].isSelected = false
        gameBoard.value[i][j].isAnimating = false
      }
    }
  }
  
  // 检查是否有匹配，如果有则重新洗牌
  if (checkMatches()) {
    removeMatches()
    fillEmptyCells()
  }
  
  // 增加步数
  steps.value++
  
  // 检查是否达到步数上限
  if (steps.value >= maxSteps.value) {
    gameOver.value = true
  }
}

// 挂载时初始化游戏
onMounted(() => {
  initGame();
})
</script>

<style scoped>
.game-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  text-align: center;
}

.game-title {
  font-size: 28px;
  margin-bottom: 20px;
  color: #333;
}

.game-board {
  display: inline-block;
  border: 5px solid #333;
  border-radius: 10px;
  padding: 10px;
  background-image: url('@/assets/xuezhiqian.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.game-row {
  display: flex;
}

.game-cell {
  width: 60px;
  height: 60px;
  margin: 5px;
}

.game-item {
  width: 100%;
  height: 100%;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  position: relative;
}

.game-icon {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 10px;
}

.game-item.is-selected {
  border: 3px solid #fff;
  box-shadow: 0 0 10px #ffd700;
  transform: scale(1.1);
}

.game-item.is-animating {
  animation: pop 0.3s ease-out forwards;
}

@keyframes pop {
  0% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.2); opacity: 0.8; }
  100% { transform: scale(0); opacity: 0; }
}

.game-timer {
  font-size: 20px;
  font-weight: bold;
  color: #e74c3c;
  margin-bottom: 10px;
}

.game-over {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 20px 40px;
  border-radius: 10px;
  text-align: center;
  z-index: 10;
}

.game-over h2 {
  margin-top: 0;
  font-size: 28px;
}

.game-over p {
  font-size: 20px;
}

.game-over button {
  margin-top: 10px;
  padding: 10px 20px;
  font-size: 16px;
  background-color: #ff6b6b;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.game-over button:hover {
  background-color: #ee5253;
}

/* 暂停和继续按钮样式 */
.pause-btn, .resume-btn {
  padding: 8px 16px;
  margin-left: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.pause-btn {
  background-color: #feca57;
  color: #2d3436;
}

.pause-btn:hover {
  background-color: #ff9f43;
}

.resume-btn {
  background-color: #1dd1a1;
  color: white;
}

.resume-btn:hover {
  background-color: #10ac84;
}

/* 暂停覆盖层样式 */
.pause-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

/* 确保继续游戏按钮在覆盖层之上 */
.resume-btn {
  position: relative;
  z-index: 1001;
}

.pause-message {
  background-color: white;
  padding: 30px;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.pause-message h2 {
  color: #2d3436;
  margin-bottom: 10px;
}

.pause-message p {
  color: #636e72;
  margin-bottom: 20px;
}

.game-item:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
}

/* 关卡升级对话框样式 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.dialog {
  background-color: white;
  padding: 30px;
  border-radius: 10px;
  text-align: center;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
  max-width: 400px;
  width: 90%;
}

.dialog h2 {
  color: #333;
  margin-top: 0;
  font-size: 24px;
}

.dialog p {
  color: #666;
  font-size: 18px;
  margin: 10px 0;
}

.dialog-buttons {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 20px;
}

.dialog-buttons button {
  padding: 10px 20px;
  font-size: 16px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.dialog-buttons button:first-child {
  background-color: #4ecdc4;
  color: white;
}

.dialog-buttons button:first-child:hover {
  background-color: #3dbdb6;
}

.dialog-buttons button:last-child {
  background-color: #ff6b6b;
  color: white;
}

.dialog-buttons button:last-child:hover {
  background-color: #ee5253;
}

.game-info {
  margin: 15px 0;
  padding: 10px;
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 10px;
  display: flex;
  justify-content: space-around;
  align-items: center;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

.game-controls {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 10px;
  flex-wrap: wrap;
}

.score {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.restart-btn {
  padding: 10px 20px;
  font-size: 16px;
  background-color: #4ecdc4;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.restart-btn:hover {
  background-color: #3daca5;
}

.shuffle-btn {
  padding: 10px 20px;
  font-size: 16px;
  background-color: #ff9f43;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-left: 10px;
}

.shuffle-btn:hover {
  background-color: #ee5253;
}

.shuffle-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>