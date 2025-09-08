<template>
  <div class="user-music-page">
    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 用户信息横幅 -->
      <div class="user-banner">
        <div class="banner-bg" :style="{ 'background-image': `url(${bannerBg})`, 'background-size': 'cover', 'background-position': 'center' }"></div>
        <div class="user-profile-container">
          <!-- 用户头像和基本信息 -->
          <div class="user-main-info">
            <div class="user-avatar-section">
              <div class="user-avatar-container" @click="changeAvatar">
                <img :src="avatarImg" alt="用户头像" class="user-avatar">
                <input 
                  type="file" 
                  ref="avatarInput" 
                  @change="onAvatarChange" 
                  accept="image/*" 
                  style="display: none;"
                />
              </div>
              <div class="user-name-container">
                <h1 class="user-name">{{ nickname }}</h1>
              </div>
            </div>
          </div>
          
          <!-- 内容导航标签 -->
          <div class="content-tabs">
            <div class="tab-item" :class="{ active: currentTab === 'liked' }" @click="switchTab('liked')">
              我喜欢
            </div>
            <div class="tab-item" :class="{ active: currentTab === 'created' }" @click="switchTab('created')">
              歌单
            </div>
            <div class="tab-item" :class="{ active: currentTab === 'albums' }" @click="switchTab('albums')">
              专辑
            </div>
            <div class="tab-item" :class="{ active: currentTab === 'videos' }" @click="switchTab('videos')">
              视频
            </div>
          </div>
          
        </div>
      </div>

      <!-- 内容区域 -->
      <div class="content-area">
        <!-- 内容头部 -->
        <div class="content-header">
          <div class="content-title">
            <h2>{{ currentTabTitle }}</h2>
            <span class="count">({{ currentTabCount }})</span>
          </div>
          <div class="action-buttons">
            <button v-if="currentTab === 'liked'" class="btn btn-secondary" @click="playAll">
              <svg class="play-icon-svg" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
                <path d="M955.733333 512L68.266667 1024V0z" fill="currentColor"></path>
              </svg>播放全部
            </button>
            <button v-if="currentTab === 'liked'" class="btn btn-secondary" @click="downloadAll">
              <i class="download-icon"></i>下载
            </button>
            <button v-if="['liked', 'created', 'albums', 'videos'].includes(currentTab)" class="btn btn-secondary" @click="showBatchOptions">
              <i class="batch-icon"></i>批量操作
            </button>
          </div>
        </div>

        <!-- 歌曲列表 -->
                  <div class="song-list-container" v-if="currentTab === 'liked'">
            <div class="song-table-header">
              <div class="table-col col-index"></div>
              <div class="table-col col-song">歌曲</div>
              <div class="table-col col-artist">歌手</div>
              <div class="table-col col-album">专辑</div>
              <div class="table-col col-duration">时长</div>
              <div class="table-col col-actions"></div>
            </div>
          <div class="song-table-body">
            <div v-for="(song, index) in likedSongs" :key="index" class="song-row" @click="playSong(song, index)">
              <div class="table-col col-index">{{ index + 1 }}</div>
              <div class="table-col col-song">
                <div class="song-info">
                  <div class="song-name">
                    {{ song.name }}
                  </div>
                </div>
              </div>
              <div class="table-col col-artist">{{ song.artist }}</div>
              <div class="table-col col-album">{{ song.album }}</div>
              <div class="table-col col-duration">{{ song.duration }}</div>
              <div class="table-col col-actions">
                <div class="more-options-container">
                  <button class="action-btn more-btn" @click.stop="toggleMoreOptions(song, index, $event)" title="更多">
                    <i class="more-icon"></i>
                  </button>
                  
                  <!-- 更多选项下拉菜单 -->
                  <div v-if="showMoreOptionsIndex === index" class="more-options-dropdown" @click.stop>
                    <div class="option-item" @click="playNext(song)">
                      <i class="option-icon play-next-icon">▶</i>
                      <span>下一首播放</span>
                    </div>
                    <div class="option-item" @click="addToQueue(song)">
                      <i class="option-icon queue-icon">+</i>
                      <span>添加到播放列表</span>
                    </div>
                    <div class="option-item" @click="downloadSong(song)">
                      <i class="option-icon download-icon">↓</i>
                      <span>下载</span>
                    </div>
                    <div class="option-item" @click="showAddToPlaylistModal(song)">
                      <i class="option-icon playlist-icon">♪</i>
                      <span>收藏到歌单</span>
                    </div>
                    <div class="option-item" @click="removeFavorite(song, index)">
                      <i class="option-icon remove-icon">×</i>
                      <span>从我喜欢中移除</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 歌单列表 -->
        <div class="playlist-list-container" v-else-if="currentTab === 'created'">
          <!-- 加载状态 -->
          <div v-if="loadingPlaylists" class="loading-playlists">
            <div class="loading-spinner"></div>
            <span>正在加载歌单...</span>
          </div>
          
          <!-- 歌单网格 -->
          <div v-else class="playlist-grid">
            <!-- 新建歌单按钮 -->
            <div class="playlist-card create-playlist-card" @click="showCreatePlaylistModal">
              <div class="create-playlist-cover">
                <div class="create-icon">
                  <i class="plus-icon">+</i>
                </div>
              </div>
              <div class="playlist-info">
                <h3 class="playlist-title">新建歌单</h3>
                <p class="playlist-desc">创建一个新的歌单</p>
              </div>
            </div>
            
            <!-- 现有歌单 -->
            <div v-for="(playlist, index) in createdPlaylists" :key="playlist.id" class="playlist-card">
              <!-- 歌单封面区域 -->
              <div class="playlist-cover" @click="goToPlaylistDetail(playlist.id)">
                <img :src="playlist.cover" alt="歌单封面">
                <div class="play-count-overlay">
                  <i class="play-icon"></i>{{ playlist.playCount }}
                </div>
              </div>
              
              <!-- 操作按钮 -->
              <div class="playlist-actions">
                <button class="action-btn delete-btn" @click.stop="showDeleteConfirm(playlist)" title="删除歌单">
                  <i class="delete-icon">×</i>
                </button>
              </div>
              
              <!-- 歌单信息区域 -->
              <div class="playlist-info" @click="goToPlaylistDetail(playlist.id)">
                <h3 class="playlist-title">{{ playlist.title }}</h3>
                <p class="playlist-desc">{{ playlist.desc }}</p>
              </div>
            </div>
            
            <!-- 空状态 -->
            <div v-if="!loadingPlaylists && createdPlaylists.length === 0" class="empty-playlists">
              <div class="empty-icon">🎵</div>
              <p>您还没有创建任何歌单</p>
              <p>点击“新建歌单”开始创建您的第一个歌单吧！</p>
            </div>
          </div>
        </div>

        <!-- 专辑列表 -->
        <div class="album-list-container" v-else-if="currentTab === 'albums'">
          <!-- 加载状态 -->
          <div v-if="loadingAlbums" class="loading-albums">
            <div class="loading-spinner"></div>
            <span>正在加载专辑...</span>
          </div>
          
          <!-- 专辑网格 -->
          <div v-else class="albums-grid">
            <div 
              v-for="album in favoriteAlbums" 
              :key="album.id" 
              class="album-card"
              @click="goToAlbumDetail(album.id)"
            >
              <div class="album-cover">
                <img :src="album.cover || '/src/assets/1音乐.png'" :alt="album.name" class="cover-image">
                <div class="album-overlay">
                  <button class="play-album-btn" @click.stop="playAlbum(album)" title="播放专辑">
                    <img src="/src/assets/开始.svg" alt="播放" class="play-icon-img" />
                  </button>
                </div>
              </div>
              <div class="album-info">
                <h3 class="album-title">{{ album.name }}</h3>
                <p class="album-artist">{{ album.artistName }}</p>
                <p class="album-date">{{ formatDate(album.releaseDate) }}</p>
              </div>
            </div>
            
            <!-- 空状态 -->
            <div v-if="!loadingAlbums && favoriteAlbums.length === 0" class="empty-albums">
              <div class="empty-icon">💿</div>
              <p>您还没有收藏任何专辑</p>
              <p>去发现一些好听的专辑吧！</p>
            </div>
          </div>
        </div>

        <!-- 视频列表 -->
        <div class="video-list-container" v-else-if="currentTab === 'videos'">
          <!-- 加载状态 -->
          <div v-if="loadingVideos" class="loading-videos">
            <div class="loading-spinner"></div>
            <span>正在加载视频...</span>
          </div>
          
          <!-- 视频网格 -->
          <div v-else class="videos-grid">
            <div 
              v-for="video in favoriteVideos" 
              :key="video.id" 
              class="video-card"
              @click="goToVideoDetail(video.id)"
            >
              <div class="video-cover">
                <img :src="video.cover || '/src/assets/1音乐.png'" :alt="video.title" class="cover-image">
                <div class="video-overlay">
                  <button class="play-video-btn" @click.stop="playVideo(video)" title="播放视频">
                    <svg class="play-icon-svg" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
                      <path d="M955.733333 512L68.266667 1024V0z" fill="currentColor"></path>
                    </svg>
                  </button>
                </div>
                <div class="video-duration">{{ formatDuration(video.duration) }}</div>
              </div>
              <div class="video-info">
                <h3 class="video-title">{{ video.title }}</h3>
                <p class="video-artist">{{ video.artistName }}</p>
                <p class="video-views">{{ formatViews(video.viewCount) }}次播放</p>
              </div>
            </div>
            
            <!-- 空状态 -->
            <div v-if="!loadingVideos && favoriteVideos.length === 0" class="empty-videos">
              <div class="empty-icon">📹</div>
              <p>您还没有收藏任何视频</p>
              <p>去发现一些精彩的MV吧！</p>
            </div>
          </div>
        </div>

        <!-- 其他标签内容 -->
        <div v-else>
          <div class="empty-state">
            <p>该内容区域正在建设中...</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 背景选择器模态框 -->
    <div v-if="showBackgroundModal" class="background-modal" @click="closeBackgroundModal">
      <div class="background-modal-content" @click.stop>
        <div class="modal-header">
          <h3>选择背景墙</h3>
          <button class="close-btn" @click="closeBackgroundModal">×</button>
        </div>
        
        <div class="background-options">
          <!-- 预设背景 -->
          <div class="option-section">
            <h4>预设背景</h4>
            <div class="background-grid">
              <div 
                v-for="(bg, index) in presetBackgrounds" 
                :key="index"
                class="background-option"
                :class="{ active: bannerBg === bg.url }"
                @click="selectBackground(bg.url)"
              >
                <img :src="bg.url" :alt="bg.name">
                <div class="bg-overlay">
                  <span class="bg-name">{{ bg.name }}</span>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 自定义上传 -->
          <div class="option-section">
            <h4>自定义背景</h4>
            <div class="custom-upload">
              <input 
                type="file" 
                ref="backgroundInput" 
                @change="onBackgroundUpload" 
                accept="image/*" 
                style="display: none;"
              />
              <button class="upload-btn" @click="$refs.backgroundInput.click()">
                <i class="upload-icon">📁</i>
                选择图片
              </button>
              <p class="upload-tip">支持 JPG、PNG 格式，建议尺寸 1920x1080</p>
            </div>
          </div>
          
          <!-- 最近使用 -->
          <div class="option-section" v-if="recentBackgrounds.length > 0">
            <h4>最近使用</h4>
            <div class="background-grid">
              <div 
                v-for="(bg, index) in recentBackgrounds" 
                :key="index"
                class="background-option"
                :class="{ active: bannerBg === bg }"
                @click="selectBackground(bg)"
              >
                <img :src="bg" alt="最近背景">
                <div class="bg-overlay">
                  <span class="bg-name">最近背景</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 新建歌单模态框 -->
    <div v-if="showCreateModal" class="create-playlist-modal" @click="closeCreatePlaylistModal">
      <div class="create-modal-content" @click.stop>
        <div class="modal-header">
          <h3>新建歌单</h3>
          <button class="close-btn" @click="closeCreatePlaylistModal">×</button>
        </div>
        
        <div class="create-form">
          <div class="form-group">
            <label for="playlistName">歌单名称</label>
            <input 
              type="text" 
              id="playlistName"
              v-model="newPlaylist.name" 
              placeholder="请输入歌单名称"
              maxlength="40"
              @keyup.enter="createNewPlaylist"
            />
            <div class="char-count">{{ newPlaylist.name.length }}/40</div>
          </div>
          
          <div class="form-group">
            <label for="playlistDesc">歌单描述</label>
            <textarea 
              id="playlistDesc"
              v-model="newPlaylist.description" 
              placeholder="请输入歌单描述（可选）"
              rows="3"
              maxlength="100"
            ></textarea>
            <div class="char-count">{{ newPlaylist.description.length }}/100</div>
          </div>
          
          <div class="form-group">
            <label>歌单封面</label>
            <div class="cover-selector">
              <div class="current-cover" @click="showCoverSelector = true">
                <img :src="newPlaylist.cover" alt="歌单封面" v-if="newPlaylist.cover">
                <div class="placeholder-cover" v-else>
                  <i class="image-icon">🖼️</i>
                  <span>点击选择封面</span>
                </div>
                <div class="cover-overlay">
                  <div class="cover-actions">
                    <button type="button" class="action-btn" @click.stop="triggerCoverUpload" title="上传图片">
                      📷
                    </button>
                    <button type="button" class="action-btn" @click.stop="showCoverSelector = true" title="选择默认封面">
                      🎨
                    </button>
                  </div>
                </div>
              </div>
              
              <!-- 封面选择器 -->
              <div v-if="showCoverSelector" class="cover-options">
                <div class="cover-tabs">
                  <button 
                    type="button"
                    class="tab-btn"
                    :class="{ active: coverTab === 'default' }"
                    @click="coverTab = 'default'"
                  >
                    默认封面
                  </button>
                  <button 
                    type="button"
                    class="tab-btn"
                    :class="{ active: coverTab === 'upload' }"
                    @click="coverTab = 'upload'"
                  >
                    上传图片
                  </button>
                </div>
                
                <!-- 默认封面选择 -->
                <div v-if="coverTab === 'default'" class="cover-grid">
                  <!-- 如果有自定义上传的封面，显示在第一位 -->
                  <div 
                    v-if="newPlaylist.cover && !defaultCovers.includes(newPlaylist.cover)"
                    class="cover-option custom-cover"
                    :class="{ active: true }"
                  >
                    <img :src="newPlaylist.cover" alt="自定义封面">
                    <div class="custom-badge">自定义</div>
                  </div>
                  
                  <!-- 默认封面选项 -->
                  <div 
                    v-for="(cover, index) in defaultCovers" 
                    :key="index"
                    class="cover-option"
                    :class="{ active: newPlaylist.cover === cover }"
                    @click="selectCover(cover)"
                  >
                    <img :src="cover" alt="默认封面">
                  </div>
                </div>
                
                <!-- 上传图片 -->
                <div v-if="coverTab === 'upload'" class="upload-area">
                  <div class="upload-zone" @click="triggerCoverUpload" @drop="handleDrop" @dragover="handleDragOver">
                    <div v-if="!uploadingCover" class="upload-content">
                      <div class="upload-icon">📷</div>
                      <p class="upload-text">点击或拖拽图片到此处</p>
                      <p class="upload-hint">支持 JPG、PNG、GIF 格式，文件大小不超过5MB</p>
                    </div>
                    <div v-else class="upload-progress">
                      <div class="progress-bar">
                        <div class="progress-fill" :style="{ width: uploadProgress + '%' }"></div>
                      </div>
                      <span class="progress-text">正在上传... {{ uploadProgress }}%</span>
                    </div>
                  </div>
                  
                  <!-- 隐藏的文件输入 -->
                  <input 
                    ref="coverInput" 
                    type="file" 
                    accept="image/jpeg,image/png,image/gif" 
                    @change="handleCoverUpload" 
                    style="display: none;"
                  />
                </div>
                
                <button class="cover-done-btn" @click="showCoverSelector = false">确定</button>
              </div>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeCreatePlaylistModal">取消</button>
          <button 
            class="btn btn-primary" 
            @click="createNewPlaylist"
            :disabled="!newPlaylist.name.trim() || isCreating"
          >
            {{ isCreating ? '创建中...' : '创建歌单' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 删除歌单确认对话框 -->
    <div v-if="showDeleteModal" class="delete-confirm-modal" @click="closeDeleteConfirm">
      <div class="delete-modal-content" @click.stop>
        <div class="modal-header">
          <h3>删除歌单</h3>
          <button class="close-btn" @click="closeDeleteConfirm">×</button>
        </div>
        
        <div class="delete-content">
          <div class="warning-icon">⚠️</div>
          <p class="warning-text">您确定要删除歌单「{{ playlistToDelete?.title }}」吗？</p>
          <p class="warning-desc">歌单删除后无法恢复，请谨慎操作。</p>
        </div>
        
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeDeleteConfirm">取消</button>
          <button 
            class="btn btn-danger" 
            @click="confirmDeletePlaylist"
            :disabled="isDeleting"
          >
            {{ isDeleting ? '删除中...' : '确认删除' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 歌曲批量操作模态框 -->
    <div v-if="showBatchModal" class="batch-modal" @click="closeBatchModal">
      <div class="batch-modal-content" @click.stop>
        <div class="modal-header">
          <h3>批量操作 - 我喜欢的歌曲</h3>
          <button class="close-btn" @click="closeBatchModal">×</button>
        </div>
        
        <div class="batch-content">
          <div class="batch-header">
            <div class="select-info">
              <span>已选择 {{ selectedSongs.length }} / {{ likedSongs.length }} 首歌曲</span>
            </div>
            <div class="batch-actions">
              <button class="btn btn-secondary" @click="selectAllSongs">
                {{ selectedSongs.length === likedSongs.length ? '取消全选' : '全选' }}
              </button>
            </div>
          </div>
          
          <div class="batch-list">
            <div v-for="(song, index) in likedSongs" :key="index" class="batch-item">
              <label class="checkbox-wrapper">
                <input 
                  type="checkbox" 
                  :checked="selectedSongs.some(s => s.id === `${song.name}_${index}`)"
                  @change="toggleSongSelection(song, index)"
                />
                <div class="song-info">
                  <span class="song-name">{{ song.name }}</span>
                  <span class="song-artist">{{ song.artist }}</span>
                </div>
              </label>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeBatchModal">取消</button>
          <button 
            class="btn btn-danger" 
            @click="batchDeleteSongs"
            :disabled="selectedSongs.length === 0"
          >
            从收藏中移除 ({{ selectedSongs.length }})
          </button>
        </div>
      </div>
    </div>

    <!-- 歌单批量操作模态框 -->
    <div v-if="showPlaylistBatchModal" class="batch-modal" @click="closeBatchModal">
      <div class="batch-modal-content" @click.stop>
        <div class="modal-header">
          <h3>批量操作 - 我创建的歌单</h3>
          <button class="close-btn" @click="closeBatchModal">×</button>
        </div>
        
        <div class="batch-content">
          <div class="batch-header">
            <div class="select-info">
              <span>已选择 {{ selectedPlaylists.length }} / {{ createdPlaylists.length }} 个歌单</span>
            </div>
            <div class="batch-actions">
              <button class="btn btn-secondary" @click="selectAllPlaylists">
                {{ selectedPlaylists.length === createdPlaylists.length ? '取消全选' : '全选' }}
              </button>
            </div>
          </div>
          
          <div class="batch-list">
            <div v-for="playlist in createdPlaylists" :key="playlist.id" class="batch-item">
              <label class="checkbox-wrapper">
                <input 
                  type="checkbox" 
                  :checked="selectedPlaylists.some(p => p.id === playlist.id)"
                  @change="togglePlaylistSelection(playlist)"
                />
                <div class="playlist-info">
                  <img :src="playlist.cover" alt="歌单封面" class="playlist-cover-small">
                  <div class="playlist-details">
                    <span class="playlist-name">{{ playlist.title }}</span>
                    <span class="playlist-desc">{{ playlist.desc }}</span>
                  </div>
                </div>
              </label>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeBatchModal">取消</button>
          <button 
            class="btn btn-danger" 
            @click="batchDeletePlaylists"
            :disabled="selectedPlaylists.length === 0"
          >
            删除歌单 ({{ selectedPlaylists.length }})
          </button>
        </div>
      </div>
    </div>

    <!-- 专辑批量操作模态框 -->
    <div v-if="showAlbumBatchModal" class="batch-modal" @click="closeBatchModal">
      <div class="batch-modal-content" @click.stop>
        <div class="modal-header">
          <h3>批量操作 - 我收藏的专辑</h3>
          <button class="close-btn" @click="closeBatchModal">×</button>
        </div>
        
        <div class="batch-content">
          <div class="batch-header">
            <div class="select-info">
              <span>已选择 {{ selectedAlbums.length }} / {{ favoriteAlbums.length }} 个专辑</span>
            </div>
            <div class="batch-actions">
              <button class="btn btn-secondary" @click="selectAllAlbums">
                {{ selectedAlbums.length === favoriteAlbums.length ? '取消全选' : '全选' }}
              </button>
            </div>
          </div>
          
          <div class="batch-list">
            <div v-for="album in favoriteAlbums" :key="album.id" class="batch-item">
              <label class="checkbox-wrapper">
                <input 
                  type="checkbox" 
                  :checked="selectedAlbums.some(a => a.id === album.id)"
                  @change="toggleAlbumSelection(album)"
                />
                <div class="playlist-info">
                  <img :src="album.cover || '/src/assets/1音乐.png'" alt="专辑封面" class="playlist-cover-small">
                  <div class="playlist-details">
                    <span class="playlist-name">{{ album.name }}</span>
                    <span class="playlist-desc">{{ album.artistName }}</span>
                  </div>
                </div>
              </label>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeBatchModal">取消</button>
          <button 
            class="btn btn-danger" 
            @click="batchRemoveAlbums"
            :disabled="selectedAlbums.length === 0"
          >
            取消收藏 ({{ selectedAlbums.length }})
          </button>
        </div>
      </div>
    </div>

    <!-- 视频批量操作模态框 -->
    <div v-if="showVideoBatchModal" class="batch-modal" @click="closeBatchModal">
      <div class="batch-modal-content" @click.stop>
        <div class="modal-header">
          <h3>批量操作 - 我收藏的视频</h3>
          <button class="close-btn" @click="closeBatchModal">×</button>
        </div>
        
        <div class="batch-content">
          <div class="batch-header">
            <div class="select-info">
              <span>已选择 {{ selectedVideos.length }} / {{ favoriteVideos.length }} 个视频</span>
            </div>
            <div class="batch-actions">
              <button class="btn btn-secondary" @click="selectAllVideos">
                {{ selectedVideos.length === favoriteVideos.length ? '取消全选' : '全选' }}
              </button>
            </div>
          </div>
          
          <div class="batch-list">
            <div v-for="video in favoriteVideos" :key="video.id" class="batch-item">
              <label class="checkbox-wrapper">
                <input 
                  type="checkbox" 
                  :checked="selectedVideos.some(v => v.id === video.id)"
                  @change="toggleVideoSelection(video)"
                />
                <div class="playlist-info">
                  <img :src="video.cover || '/src/assets/1音乐.png'" alt="视频封面" class="playlist-cover-small">
                  <div class="playlist-details">
                    <span class="playlist-name">{{ video.title }}</span>
                    <span class="playlist-desc">{{ video.artistName }}</span>
                  </div>
                </div>
              </label>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeBatchModal">取消</button>
          <button 
            class="btn btn-danger" 
            @click="batchRemoveVideos"
            :disabled="selectedVideos.length === 0"
          >
            取消收藏 ({{ selectedVideos.length }})
          </button>
        </div>
      </div>
    </div>

    <!-- 下载确认模态框 -->
    <div v-if="showDownloadModal" class="download-modal" @click="closeDownloadModal">
      <div class="download-modal-content" @click.stop>
        <div class="modal-header">
          <h3>下载歌曲</h3>
          <button class="close-btn" @click="closeDownloadModal">×</button>
        </div>
        
        <div class="download-content">
          <div class="download-info">
            <div class="info-item">
              <i class="info-icon">🎵</i>
              <span>准备下载 {{ downloadSongs_list.length }} 首歌曲</span>
            </div>
            <div class="info-item">
              <i class="info-icon">📁</i>
              <span>文件将保存到浏览器默认下载目录</span>
            </div>
            <div class="info-item">
              <i class="info-icon">ℹ️</i>
              <span>支持格式：MP3、文件名格式：歌手 - 歌曲名.mp3</span>
            </div>
          </div>
          
          <!-- 下载进度条 -->
          <div v-if="isDownloading" class="download-progress">
            <div class="progress-info">
              <span>下载中...</span>
              <span>{{ downloadProgress }}%</span>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: downloadProgress + '%' }"></div>
            </div>
            <div class="download-status">
              <span>成功: {{ downloadResults.success }}</span>
              <span>失败: {{ downloadResults.failed }}</span>
              <span>跳过: {{ downloadResults.skipped }}</span>
            </div>
          </div>
          
          <!-- 歌曲列表 -->
          <div class="download-song-list">
            <div class="list-header">
              <h4>下载列表 ({{ downloadSongs_list.length }} 首)</h4>
            </div>
            <div class="song-list">
              <div v-for="(song, index) in downloadSongs_list.slice(0, 5)" :key="index" class="download-song-item">
                <div class="song-info">
                  <span class="song-name">{{ song.name }}</span>
                  <span class="song-artist">{{ song.artist }}</span>
                </div>
                <span class="file-status">
                  <i v-if="isSongDownloadable(song)" class="status-icon ready">✓</i>
                  <i v-else class="status-icon unavailable">×</i>
                </span>
              </div>
              <div v-if="downloadSongs_list.length > 5" class="more-songs">
                <span>还有 {{ downloadSongs_list.length - 5 }} 首歌曲...</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeDownloadModal" :disabled="isDownloading">取消</button>
          <button 
            class="btn btn-primary" 
            @click="confirmDownloadSongs"
            :disabled="downloadSongs_list.length === 0 || isDownloading"
          >
            {{ isDownloading ? '下载中...' : `开始下载 (${downloadSongs_list.length})` }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onBeforeUnmount, computed, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { handleAvatarUpload, handleBackgroundUpload, triggerFileSelect, FILE_TYPES } from '@/utils/fileUpload.js'
import { ElMessage } from 'element-plus'
import { initFavoriteSongs, getFavoriteSongs, refreshFavoriteSongs, getUserId, favoriteStatus } from '@/utils/favoriteManager.js'
import { createPlaylist, getUserPlaylists, deletePlaylist } from '@/api/playlist.js'
import { favoriteSong } from '@/api/song.js'
import { downloadSongs, getDownloadableSongs, showDownloadSummary, isSongDownloadable } from '@/utils/downloadManager.js'
import { addToPlaylist, addMultipleToPlaylist } from '@/utils/musicPlayer.js'

export default {
  components: {
  },
  setup() {
    const router = useRouter()
    const avatarImg = ref(localStorage.getItem('userAvatar') || 'https://q1.qlogo.cn/g?b=qq&nk=10000&s=100')
    const nickname = ref(localStorage.getItem('nickname') || localStorage.getItem('username') || '用户昵称')
    const userBio = ref(localStorage.getItem('userBio') || '热爱音乐，分享美好。')

    const bannerBg = ref(localStorage.getItem('userBannerBg') || 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=1920&h=1080&fit=crop&q=80') // 背景图
    const currentTab = ref('liked')
    const avatarInput = ref(null)
    
    // 更多选项相关状态
    const showMoreOptionsIndex = ref(-1)

    // 新建歌单相关状态
    const showCreateModal = ref(false)
    const showCoverSelector = ref(false)
    const isCreating = ref(false)
    const coverTab = ref('default')
    const uploadingCover = ref(false)
    const uploadProgress = ref(0)
    const coverInput = ref(null)
    
    // 删除歌单相关状态
    const showDeleteModal = ref(false)
    const isDeleting = ref(false)
    const playlistToDelete = ref(null)
    
    // 批量操作相关状态
    const showBatchModal = ref(false)
    const showPlaylistBatchModal = ref(false)
    const showAlbumBatchModal = ref(false)
    const showVideoBatchModal = ref(false)
    const selectedSongs = ref([])
    const selectedPlaylists = ref([])
    const selectedAlbums = ref([])
    const selectedVideos = ref([])
    const isSelectMode = ref(false)
    
    // 下载相关状态
    const showDownloadModal = ref(false)
    const downloadSongs_list = ref([])
    const isDownloading = ref(false)
    const downloadProgress = ref(0)
    const currentDownloadIndex = ref(0)
    const downloadResults = ref({
      total: 0,
      success: 0,
      failed: 0,
      skipped: 0
    })
    
    // 新建歌单表单数据
    const newPlaylist = ref({
      name: '',
      description: '',
      cover: 'https://picsum.photos/300/300?random=100'
    })
    
    // 默认封面选项
    const defaultCovers = ref([
      'https://picsum.photos/300/300?random=100',
      'https://picsum.photos/300/300?random=101',
      'https://picsum.photos/300/300?random=102',
      'https://picsum.photos/300/300?random=103',
      'https://picsum.photos/300/300?random=104',
      'https://picsum.photos/300/300?random=105',
      'https://picsum.photos/300/300?random=106',
      'https://picsum.photos/300/300?random=107'
    ])

    // 用户喜欢的歌曲列表（从 favoriteManager 获取）
    const likedSongs = computed(() => getFavoriteSongs())

    // 用户创建的歌单列表（从API动态获取）
    const createdPlaylists = ref([])
    const loadingPlaylists = ref(false)

    // 用户收藏的专辑列表
    const favoriteAlbums = ref([])
    const loadingAlbums = ref(false)

    // 用户收藏的视频列表
    const favoriteVideos = ref([])
    const loadingVideos = ref(false)

    // 计算属性
    const currentTabTitle = computed(() => {
        switch(currentTab.value) {
          case 'liked': return '我喜欢的歌曲'
          case 'created': return '我创建的歌单'
          case 'albums': return '我收藏的专辑'
          case 'videos': return '我收藏的视频'
          default: return ''
        }
      })

    const currentTabCount = computed(() => {
        switch(currentTab.value) {
          case 'liked': return likedSongs.value.length
          case 'created': return createdPlaylists.value.length
          case 'albums': return favoriteAlbums.value.length
          case 'videos': return favoriteVideos.value.length
          default: return 0
        }
      })

    // 方法
    const switchTab = (tab) => {
      currentTab.value = tab
      
      // 根据标签页加载对应数据
      if (tab === 'created' && createdPlaylists.value.length === 0) {
        loadUserPlaylists()
      } else if (tab === 'albums' && favoriteAlbums.value.length === 0) {
        loadFavoriteAlbums()
      } else if (tab === 'videos' && favoriteVideos.value.length === 0) {
        loadFavoriteVideos()
      }
    }

    const changeAvatar = () => {
      triggerFileSelect(avatarInput, { accept: FILE_TYPES.AVATAR })
    }

    const onAvatarChange = async (e) => {
      const file = e.target.files[0]
      if (!file) return

      try {
        const dataURL = await handleAvatarUpload(file, (url) => {
          avatarImg.value = url
          ElMessage.success('头像更新成功！')
        })
      } catch (error) {
        ElMessage.error(error.message || '头像上传失败')
      }
    }

    const updateAvatar = () => {
      avatarImg.value = localStorage.getItem('userAvatar') || 'https://q1.qlogo.cn/g?b=qq&nk=10000&s=100'
    }

    const updateNickname = () => {
      nickname.value = localStorage.getItem('userNickname') || '用户昵称'
    }

    const updateBio = () => {
      userBio.value = localStorage.getItem('userBio') || '热爱音乐，分享美好。'
    }

    const updateBackground = (event) => {
      bannerBg.value = event.detail.url
    }

    const playSong = (song, index) => {
      if (song && song.id) {
        // 添加到播放列表并立即播放
        const success = addToPlaylist({
          id: song.id,
          name: song.name,
          artist: song.artist || '未知歌手',
          album: song.album || '未知专辑',
          duration: song.duration || 0,
          cover: song.cover || '/src/assets/1音乐.png',
          audioUrl: song.audioUrl || song.filePath || ''
        }, true) // 立即播放
        
        if (success) {
          ElMessage.success(`开始播放：${song.name}`)
        }
      } else {
        ElMessage.warning('歌曲信息不完整')
      }
    }

    const playAll = () => {
      if (currentTab.value === 'liked' && likedSongs.value.length > 0) {
        // 格式化歌曲数据并批量添加到播放列表
        const formattedSongs = likedSongs.value.map(song => ({
          id: song.id,
          name: song.name,
          artist: song.artist || '未知歌手',
          album: song.album || '未知专辑',
          duration: song.duration || 0,
          cover: song.cover || '/src/assets/1音乐.png',
          audioUrl: song.audioUrl || song.filePath || ''
        }))
        
        // 添加到播放列表并播放第一首
        const success = addMultipleToPlaylist(formattedSongs, true)
        
        if (success) {
          ElMessage.success(`开始播放我喜欢的音乐，共${likedSongs.value.length}首歌曲`)
        }
      } else if (currentTab.value === 'created') {
        ElMessage.info('歌单播放功能开发中，敬请期待')
      } else {
        ElMessage.warning('暂无可播放的内容')
      }
    }

    // 切换更多选项下拉菜单
    const toggleMoreOptions = (song, index, event) => {
      if (showMoreOptionsIndex.value === index) {
        showMoreOptionsIndex.value = -1
      } else {
        showMoreOptionsIndex.value = index
        
        // 计算下拉框位置
        nextTick(() => {
          const button = event.currentTarget
          const dropdown = button.parentElement.querySelector('.more-options-dropdown')
          if (dropdown && button) {
            const buttonRect = button.getBoundingClientRect()
            dropdown.style.top = `${buttonRect.bottom + 8}px`
            dropdown.style.left = `${buttonRect.right - 180}px` // 180是下拉框的宽度
          }
        })
      }
    }

    // 下一首播放功能
    const playNext = (song) => {
      if (song && song.id) {
        try {
          // 获取当前播放列表
          const currentPlaylist = JSON.parse(localStorage.getItem('currentPlaylist') || '[]')
          const currentIndex = parseInt(localStorage.getItem('currentSongIndex') || '0')
          
          // 格式化歌曲数据
          const formattedSong = {
            id: song.id,
            name: song.name,
            artist: song.artist || '未知歌手',
            album: song.album || '未知专辑',
            duration: song.duration || 0,
            cover: song.cover || '/src/assets/1音乐.png',
            audioUrl: song.audioUrl || song.filePath || ''
          }
          
          // 将歌曲插入到当前播放位置的下一首
          const newPlaylist = [...currentPlaylist]
          newPlaylist.splice(currentIndex + 1, 0, formattedSong)
          
          // 更新localStorage
          localStorage.setItem('currentPlaylist', JSON.stringify(newPlaylist))
          
          // 触发播放列表更新事件
          window.dispatchEvent(new CustomEvent('playlistUpdated', {
            detail: { playlist: newPlaylist }
          }))
          
          ElMessage.success(`已将《${song.name}》设为下一首播放`)
        } catch (error) {
          console.error('设置下一首播放失败:', error)
          ElMessage.error('设置下一首播放失败')
        }
      } else {
        ElMessage.warning('歌曲信息不完整')
      }
      showMoreOptionsIndex.value = -1
    }

    // 添加到播放列表
    const addToQueue = (song) => {
      if (song && song.id) {
        const success = addToPlaylist({
          id: song.id,
          name: song.name,
          artist: song.artist || '未知歌手',
          album: song.album || '未知专辑',
          duration: song.duration || 0,
          cover: song.cover || '/src/assets/1音乐.png',
          audioUrl: song.audioUrl || song.filePath || ''
        }, false) // 不立即播放
        
        if (success) {
          ElMessage.success(`已将《${song.name}》添加到播放列表`)
        }
      } else {
        ElMessage.warning('歌曲信息不完整')
      }
      showMoreOptionsIndex.value = -1
    }

    // 显示添加到歌单模态框
    const showAddToPlaylistModal = (song) => {
      // 这里可以实现添加到歌单的功能
      ElMessage.info('添加到歌单功能开发中，敬请期待')
      showMoreOptionsIndex.value = -1
    }

    // 从我喜欢中移除
    const removeFavorite = async (song, index) => {
      try {
        const userId = getUserId()
        if (!userId) {
          ElMessage.warning('请先登录')
          return
        }

        const response = await favoriteSong(userId, song.id, 'unlike')
        if (response && response.code === 200) {
          // 从界面列表中移除
          likedSongs.value.splice(index, 1)
          
          // 更新收藏状态映射
          favoriteStatus.set(song.id, false)
          
          // 触发全局事件通知其他组件更新
          window.dispatchEvent(new CustomEvent('songLikeChanged', {
            detail: { 
              songId: song.id, 
              isLiked: false
            }
          }))
          
          ElMessage.success(`已将《${song.name}》从我喜欢中移除`)
        } else {
          ElMessage.error('移除失败：' + (response?.message || '请重试'))
        }
      } catch (error) {
        console.error('移除收藏失败:', error)
        ElMessage.error('移除失败，请检查网络连接')
      }
      showMoreOptionsIndex.value = -1
    }

    const goToPlaylistDetail = (id) => {
      router.push({ path: `/playlist/${id}` })
    }

    const showAddToModal = () => {
      // 显示添加到歌单模态框
    }

    // 真正的下载功能
    const downloadAll = () => {
      if (currentTab.value === 'liked') {
        if (likedSongs.value.length === 0) {
          ElMessage.warning('暂无可下载的歌曲')
          return
        }
        
        // 过滤可下载的歌曲
        const downloadableSongs = getDownloadableSongs(likedSongs.value)
        
        if (downloadableSongs.length === 0) {
          ElMessage.warning('暂无可下载的音频文件')
          return
        }
        
        // 显示下载确认模态框
        downloadSongs_list.value = downloadableSongs
        showDownloadModal.value = true
        
      } else if (currentTab.value === 'created') {
        if (createdPlaylists.value.length === 0) {
          ElMessage.warning('暂无可下载的歌单')
          return
        }
        ElMessage.info('歌单批量下载功能开发中，敬请期待')
      }
    }
    
    // 下载单首歌曲
    const downloadSong = (song) => {
      if (!isSongDownloadable(song)) {
        ElMessage.warning('该歌曲暂无可下载的音频文件')
        return
      }
      
      downloadSongs_list.value = [song]
      showDownloadModal.value = true
    }
    
    // 显示下载模态框
    const showDownloadConfirm = (songs) => {
      downloadSongs_list.value = songs
      showDownloadModal.value = true
    }
    
    // 关闭下载模态框
    const closeDownloadModal = () => {
      if (!isDownloading.value) {
        showDownloadModal.value = false
        downloadSongs_list.value = []
        resetDownloadProgress()
      }
    }
    
    // 重置下载进度
    const resetDownloadProgress = () => {
      downloadProgress.value = 0
      currentDownloadIndex.value = 0
      downloadResults.value = {
        total: 0,
        success: 0,
        failed: 0,
        skipped: 0
      }
    }
    
    // 确认下载歌曲
    const confirmDownloadSongs = async () => {
      if (downloadSongs_list.value.length === 0) {
        ElMessage.warning('暂无可下载的歌曲')
        return
      }
      
      try {
        isDownloading.value = true
        resetDownloadProgress()
        
        console.log('🇝 开始下载歌曲:', downloadSongs_list.value.length, '首')
        
        // 调用下载管理器
        const results = await downloadSongs(
          downloadSongs_list.value,
          // 总体进度回调
          (progress, currentResults) => {
            downloadProgress.value = progress
            downloadResults.value = currentResults
          },
          // 单个文件进度回调
          (index, song, status, progress = 0) => {
            currentDownloadIndex.value = index
            console.log(`下载进度: ${song.name} - ${status} (${progress}%)`)
          }
        )
        
        // 显示下载结果
        showDownloadSummary(results)
        
        // 下载完成后稍后关闭模态框
        setTimeout(() => {
          closeDownloadModal()
        }, 2000)
        
      } catch (error) {
        console.error('下载歌曲失败:', error)
        ElMessage.error('下载失败，请稍后重试')
      } finally {
        isDownloading.value = false
      }
    }

    const showBatchOptions = () => {
      if (currentTab.value === 'liked') {
        if (likedSongs.value.length === 0) {
          ElMessage.warning('暂无歌曲可进行批量操作')
          return
        }
        showBatchModal.value = true
      } else if (currentTab.value === 'created') {
        if (createdPlaylists.value.length === 0) {
          ElMessage.warning('暂无歌单可进行批量操作')
          return
        }
        showPlaylistBatchModal.value = true
      } else if (currentTab.value === 'albums') {
        if (favoriteAlbums.value.length === 0) {
          ElMessage.warning('暂无专辑可进行批量操作')
          return
        }
        showAlbumBatchModal.value = true
      } else if (currentTab.value === 'videos') {
        if (favoriteVideos.value.length === 0) {
          ElMessage.warning('暂无视频可进行批量操作')
          return
        }
        showVideoBatchModal.value = true
      }
    }

    // 新建歌单相关方法
    const showCreatePlaylistModal = () => {
      showCreateModal.value = true
      // 重置表单数据
      newPlaylist.value = {
        name: '',
        description: '',
        cover: 'https://picsum.photos/300/300?random=100'
      }
      showCoverSelector.value = false
      coverTab.value = 'default'
      uploadingCover.value = false
      uploadProgress.value = 0
    }

    const closeCreatePlaylistModal = () => {
      showCreateModal.value = false
      showCoverSelector.value = false
    }

    const selectCover = (cover) => {
      newPlaylist.value.cover = cover
    }

    // 触发封面上传
    const triggerCoverUpload = () => {
      coverInput.value?.click()
    }

    // 处理封面文件上传
    const handleCoverUpload = async (event) => {
      const file = event.target.files[0]
      if (!file) return

      // 验证文件类型
      const validTypes = ['image/jpeg', 'image/png', 'image/gif']
      if (!validTypes.includes(file.type)) {
        ElMessage.error('请选择 JPG、PNG 或 GIF 格式的图片')
        return
      }

      // 验证文件大小 (5MB)
      const maxSize = 5 * 1024 * 1024
      if (file.size > maxSize) {
        ElMessage.error('图片大小不能超过 5MB')
        return
      }

      try {
        uploadingCover.value = true
        uploadProgress.value = 0

        // 使用FileReader读取文件为Base64
        const reader = new FileReader()
        
        reader.onload = (e) => {
          const base64Data = e.target.result
          newPlaylist.value.cover = base64Data
          ElMessage.success('封面上传成功')
          
          // 切换到默认封面标签页以显示上传的图片
          coverTab.value = 'default'
          uploadingCover.value = false
          uploadProgress.value = 100
        }

        reader.onerror = () => {
          throw new Error('文件读取失败')
        }

        // 模拟上传进度
        const progressInterval = setInterval(() => {
          if (uploadProgress.value < 90) {
            uploadProgress.value += 10
          }
        }, 50)

        // 读取文件
        reader.readAsDataURL(file)
        
        // 清除进度定时器
        setTimeout(() => {
          clearInterval(progressInterval)
        }, 500)

      } catch (error) {
        console.error('封面上传失败:', error)
        ElMessage.error('封面上传失败: ' + (error.message || '请重试'))
        uploadingCover.value = false
        uploadProgress.value = 0
      } finally {
        // 重置文件输入框
        if (coverInput.value) {
          coverInput.value.value = ''
        }
      }
    }

    // 处理拖拽上传
    const handleDragOver = (event) => {
      event.preventDefault()
    }

    const handleDrop = (event) => {
      event.preventDefault()
      const files = event.dataTransfer.files
      if (files.length > 0) {
        const file = files[0]
        // 创建一个模拟的 change 事件
        const fakeEvent = {
          target: {
            files: [file]
          }
        }
        handleCoverUpload(fakeEvent)
      }
    }

    // 获取用户创建的歌单列表
    const fetchUserPlaylists = async () => {
      try {
        loadingPlaylists.value = true
        const userId = parseInt(localStorage.getItem('userId') || '1')
        
        const response = await getUserPlaylists(userId)
        
        if (response && response.code === 200) {
          // 按照前后端API交互规范处理数据
          let playlistsData = []
          if (response.data && response.data.content && Array.isArray(response.data.content)) {
            playlistsData = response.data.content
          } else if (response.data && Array.isArray(response.data)) {
            playlistsData = response.data
          }
          
          // 转换为前端需要的数据结构
          createdPlaylists.value = playlistsData.map(playlist => ({
            id: playlist.id,
            title: playlist.name,
            cover: playlist.coverUrl || 'https://picsum.photos/300/300?random=1',
            desc: playlist.description || '暂无描述',
            playCount: playlist.playCount || 0
          }))
          
          console.log('🎵 用户歌单加载完成:', createdPlaylists.value.length, '个歌单')
        } else {
          console.warn('获取用户歌单失败:', response?.message)
          createdPlaylists.value = []
        }
      } catch (error) {
        console.error('获取用户歌单失败:', error)
        // 网络错误已在httpUtils中处理，这里不重复显示
        createdPlaylists.value = []
      } finally {
        loadingPlaylists.value = false
      }
    }

    const createNewPlaylist = async () => {
      if (!newPlaylist.value.name.trim()) {
        ElMessage.warning('请输入歌单名称')
        return
      }

      try {
        isCreating.value = true
        
        // 从 localStorage 获取用户ID，如果没有则使用默认值1
        const userId = parseInt(localStorage.getItem('userId') || '1')
        
        const response = await createPlaylist(
          newPlaylist.value.name.trim(),
          newPlaylist.value.cover,
          newPlaylist.value.description.trim(),
          userId
        )

        if (response && response.code === 200) {
          ElMessage.success('歌单创建成功！')
          closeCreatePlaylistModal()
          
          // 重新获取用户歌单列表，确保数据同步
          await fetchUserPlaylists()
        } else {
          ElMessage.error('创建歌单失败：' + (response?.message || '请重试'))
        }
      } catch (error) {
        console.error('创建歌单失败:', error)
        if (error.response?.data?.message) {
          ElMessage.error('创建歌单失败：' + error.response.data.message)
        } else {
          ElMessage.error('创建歌单失败，请检查网络连接')
        }
      } finally {
        isCreating.value = false
      }
    }

    // 删除歌单相关方法
    const showDeleteConfirm = (playlist) => {
      playlistToDelete.value = playlist
      showDeleteModal.value = true
    }

    const closeDeleteConfirm = () => {
      showDeleteModal.value = false
      playlistToDelete.value = null
    }

    const confirmDeletePlaylist = async () => {
      if (!playlistToDelete.value) return

      try {
        isDeleting.value = true
        
        const response = await deletePlaylist(playlistToDelete.value.id)
        
        if (response && response.code === 200) {
          ElMessage.success('歌单删除成功！')
          closeDeleteConfirm()
          
          // 重新获取用户歌单列表，确保数据同步
          await fetchUserPlaylists()
        } else {
          ElMessage.error('删除歌单失败：' + (response?.message || '请重试'))
        }
      } catch (error) {
        console.error('删除歌单失败:', error)
        if (error.response?.data?.message) {
          ElMessage.error('删除歌单失败：' + error.response.data.message)
        } else {
          ElMessage.error('删除歌单失败，请检查网络连接')
        }
      } finally {
        isDeleting.value = false
      }
    }

    // 批量操作相关方法
    const toggleSelectMode = () => {
      isSelectMode.value = !isSelectMode.value
      if (!isSelectMode.value) {
        selectedSongs.value = []
        selectedPlaylists.value = []
      }
    }

    const toggleSongSelection = (song, index) => {
      const songId = `${song.name}_${index}`
      const existingIndex = selectedSongs.value.findIndex(s => s.id === songId)
      
      if (existingIndex > -1) {
        selectedSongs.value.splice(existingIndex, 1)
      } else {
        selectedSongs.value.push({ 
          ...song, 
          id: songId, 
          songId: song.id, // 确保有真实的歌曲ID
          index 
        })
      }
    }

    const togglePlaylistSelection = (playlist) => {
      const existingIndex = selectedPlaylists.value.findIndex(p => p.id === playlist.id)
      
      if (existingIndex > -1) {
        selectedPlaylists.value.splice(existingIndex, 1)
      } else {
        selectedPlaylists.value.push(playlist)
      }
    }

    const selectAllSongs = () => {
      if (selectedSongs.value.length === likedSongs.value.length) {
        selectedSongs.value = []
      } else {
        selectedSongs.value = likedSongs.value.map((song, index) => ({
          ...song,
          id: `${song.name}_${index}`,
          songId: song.id, // 确保有真实的歌曲ID
          index
        }))
      }
    }

    const selectAllPlaylists = () => {
      if (selectedPlaylists.value.length === createdPlaylists.value.length) {
        selectedPlaylists.value = []
      } else {
        selectedPlaylists.value = [...createdPlaylists.value]
      }
    }

    const batchDeleteSongs = async () => {
      if (selectedSongs.value.length === 0) {
        ElMessage.warning('请选择要删除的歌曲')
        return
      }

      try {
        console.log('开始批量删除收藏歌曲:', selectedSongs.value)
        
        // 获取用户ID
        const userId = getUserId()
        if (!userId) {
          ElMessage.warning('请先登录')
          return
        }

        // 批量调用取消收藏API
        const deletePromises = selectedSongs.value.map(async (selectedSong) => {
          try {
            const response = await favoriteSong(userId, selectedSong.songId, 'unlike')
            if (response && response.code === 200) {
              console.log(`成功取消收藏歌曲 ${selectedSong.songId}`)
              return selectedSong.songId
            } else {
              console.error(`取消收藏歌曲 ${selectedSong.songId} 失败:`, response?.message)
              return null
            }
          } catch (error) {
            console.error(`取消收藏歌曲 ${selectedSong.songId} 失败:`, error)
            return null
          }
        })

        const results = await Promise.all(deletePromises)
        const successCount = results.filter(result => result !== null).length
        const failCount = results.filter(result => result === null).length

        if (successCount > 0) {
          // 从界面列表中移除成功删除的歌曲
          const successfullyDeletedIds = results.filter(result => result !== null)
          likedSongs.value = likedSongs.value.filter(song => 
            !successfullyDeletedIds.includes(song.id)
          )
          
          // 更新收藏状态映射
          successfullyDeletedIds.forEach(songId => {
            favoriteStatus.set(songId, false)
          })
          
          // 触发全局事件通知其他组件更新
          successfullyDeletedIds.forEach(songId => {
            window.dispatchEvent(new CustomEvent('songLikeChanged', {
              detail: { 
                songId: songId, 
                isLiked: false
              }
            }))
          })
          
          ElMessage.success(`成功从收藏中移除 ${successCount} 首歌曲${failCount > 0 ? `，${failCount} 首失败` : ''}`)
        } else {
          ElMessage.error('批量删除失败，请稍后重试')
        }
        
        // 清空选中状态
        selectedSongs.value = []
        isSelectMode.value = false
        showBatchModal.value = false
        
      } catch (error) {
        console.error('批量删除收藏歌曲失败:', error)
        let shouldShowError = true
        let errorMessage = '批量删除失败'
        
        if (error.message === 'Network Error' || error.code === 'ECONNABORTED' || error.code === 'ECONNREFUSED') {
          shouldShowError = false // 网络错误已在httpUtils.js中处理
        } else if (error.response?.data?.message) {
          errorMessage = error.response.data.message
        } else if (error.message) {
          errorMessage = error.message
        }
        
        if (shouldShowError) {
          ElMessage.error(errorMessage)
        }
      }
    }

    const batchDeletePlaylists = async () => {
      if (selectedPlaylists.value.length === 0) {
        ElMessage.warning('请选择要删除的歌单')
        return
      }

      try {
        for (const playlist of selectedPlaylists.value) {
          await deletePlaylist(playlist.id)
        }
        ElMessage.success(`成功删除 ${selectedPlaylists.value.length} 个歌单`)
        selectedPlaylists.value = []
        showPlaylistBatchModal.value = false
        await fetchUserPlaylists()
      } catch (error) {
        ElMessage.error('批量删除歌单失败')
      }
    }

    const closeBatchModal = () => {
      showBatchModal.value = false
      showPlaylistBatchModal.value = false
      showAlbumBatchModal.value = false
      showVideoBatchModal.value = false
      selectedSongs.value = []
      selectedPlaylists.value = []
      selectedAlbums.value = []
      selectedVideos.value = []
      isSelectMode.value = false
    }

    // 专辑批量操作方法
    const toggleAlbumSelection = (album) => {
      const existingIndex = selectedAlbums.value.findIndex(a => a.id === album.id)
      
      if (existingIndex > -1) {
        selectedAlbums.value.splice(existingIndex, 1)
      } else {
        selectedAlbums.value.push(album)
      }
    }

    const selectAllAlbums = () => {
      if (selectedAlbums.value.length === favoriteAlbums.value.length) {
        selectedAlbums.value = []
      } else {
        selectedAlbums.value = [...favoriteAlbums.value]
      }
    }

    const batchRemoveAlbums = async () => {
      if (selectedAlbums.value.length === 0) {
        ElMessage.warning('请选择要取消收藏的专辑')
        return
      }

      try {
        // 这里应该调用取消收藏专辑的API
        // 暂时模拟操作
        ElMessage.success(`成功取消收藏 ${selectedAlbums.value.length} 个专辑`)
        
        // 从列表中移除
        selectedAlbums.value.forEach(album => {
          const index = favoriteAlbums.value.findIndex(a => a.id === album.id)
          if (index > -1) {
            favoriteAlbums.value.splice(index, 1)
          }
        })
        
        selectedAlbums.value = []
        showAlbumBatchModal.value = false
      } catch (error) {
        ElMessage.error('批量取消收藏专辑失败')
      }
    }

    // 视频批量操作方法
    const toggleVideoSelection = (video) => {
      const existingIndex = selectedVideos.value.findIndex(v => v.id === video.id)
      
      if (existingIndex > -1) {
        selectedVideos.value.splice(existingIndex, 1)
      } else {
        selectedVideos.value.push(video)
      }
    }

    const selectAllVideos = () => {
      if (selectedVideos.value.length === favoriteVideos.value.length) {
        selectedVideos.value = []
      } else {
        selectedVideos.value = [...favoriteVideos.value]
      }
    }

    const batchRemoveVideos = async () => {
      if (selectedVideos.value.length === 0) {
        ElMessage.warning('请选择要取消收藏的视频')
        return
      }

      try {
        // 这里应该调用取消收藏视频的API
        // 暂时模拟操作
        ElMessage.success(`成功取消收藏 ${selectedVideos.value.length} 个视频`)
        
        // 从列表中移除
        selectedVideos.value.forEach(video => {
          const index = favoriteVideos.value.findIndex(v => v.id === video.id)
          if (index > -1) {
            favoriteVideos.value.splice(index, 1)
          }
        })
        
        selectedVideos.value = []
        showVideoBatchModal.value = false
      } catch (error) {
        ElMessage.error('批量取消收藏视频失败')
      }
    }



    // 点击外部关闭下拉菜单
    const handleClickOutside = (event) => {
      if (!event.target.closest('.more-options-container')) {
        showMoreOptionsIndex.value = -1
      }
    }

    // 生命周期
    onMounted(async () => {
      // 初始化用户喜欢的歌曲
      await initFavoriteSongs()
      
      // 获取用户创建的歌单列表
      await fetchUserPlaylists()
      
      window.addEventListener('user-avatar-changed', updateAvatar)
      window.addEventListener('user-nickname-changed', updateNickname)
      window.addEventListener('user-bio-changed', updateBio)
      window.addEventListener('background-changed', updateBackground)
      
      // 监听歌曲喜欢状态变化
      window.addEventListener('songLikeChanged', refreshFavoriteSongs)
      
      // 监听点击外部关闭下拉菜单
      document.addEventListener('click', handleClickOutside)
      
      // 为body添加我的音乐页面样式类
      document.body.classList.add('my-music-page')
    })

    // 加载收藏的专辑
    const loadFavoriteAlbums = async () => {
      loadingAlbums.value = true
      try {
        // 模拟API调用，实际应该从后端获取用户收藏的专辑
        await new Promise(resolve => setTimeout(resolve, 1000))
        favoriteAlbums.value = [
          {
            id: 1,
            name: 'U-87',
            artistName: '陈奕迅',
            cover: 'https://picsum.photos/300/300?random=201',
            releaseDate: '2005-06-07'
          },
          {
            id: 2,
            name: '十二新作',
            artistName: '周杰伦',
            cover: 'https://picsum.photos/300/300?random=202',
            releaseDate: '2012-12-28'
          }
        ]
      } catch (error) {
        console.error('加载收藏专辑失败:', error)
      } finally {
        loadingAlbums.value = false
      }
    }

    // 加载收藏的视频
    const loadFavoriteVideos = async () => {
      loadingVideos.value = true
      try {
        // 模拟API调用，实际应该从后端获取用户收藏的视频
        await new Promise(resolve => setTimeout(resolve, 1000))
        favoriteVideos.value = [
          {
            id: 1,
            title: '浮夸',
            artistName: '陈奕迅',
            cover: 'https://picsum.photos/300/200?random=301',
            duration: 268,
            viewCount: 1234567
          },
          {
            id: 2,
            title: '青花瓷',
            artistName: '周杰伦',
            cover: 'https://picsum.photos/300/200?random=302',
            duration: 235,
            viewCount: 9876543
          }
        ]
      } catch (error) {
        console.error('加载收藏视频失败:', error)
      } finally {
        loadingVideos.value = false
      }
    }

    // 跳转到专辑详情
    const goToAlbumDetail = (albumId) => {
      router.push(`/album/${albumId}`)
    }

    // 跳转到视频详情
    const goToVideoDetail = (videoId) => {
      router.push(`/mv/${videoId}`)
    }

    // 播放专辑
    const playAlbum = async (album) => {
      try {
        // 这里应该获取专辑的所有歌曲并添加到播放列表
        console.log('播放专辑:', album.name)
        ElMessage.success(`开始播放专辑《${album.name}》`)
      } catch (error) {
        console.error('播放专辑失败:', error)
        ElMessage.error('播放失败')
      }
    }

    // 播放视频
    const playVideo = async (video) => {
      try {
        console.log('播放视频:', video.title)
        ElMessage.success(`开始播放视频《${video.title}》`)
      } catch (error) {
        console.error('播放视频失败:', error)
        ElMessage.error('播放失败')
      }
    }

    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return '未知'
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN')
    }

    // 格式化播放次数
    const formatViews = (count) => {
      if (count >= 10000) {
        return Math.floor(count / 10000) + '万'
      }
      return count.toString()
    }

    // 格式化时长
    const formatDuration = (seconds) => {
      if (!seconds) return '00:00'
      const minutes = Math.floor(seconds / 60)
      const remainingSeconds = seconds % 60
      return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`
    }

    onBeforeUnmount(() => {
      window.removeEventListener('user-avatar-changed', updateAvatar)
      window.removeEventListener('user-nickname-changed', updateNickname)
      window.removeEventListener('user-bio-changed', updateBio)
      window.removeEventListener('background-changed', updateBackground)
      window.removeEventListener('songLikeChanged', refreshFavoriteSongs)
      
      // 移除点击外部关闭下拉菜单的监听器
      document.removeEventListener('click', handleClickOutside)
      
      // 移除我的音乐页面样式类
      document.body.classList.remove('my-music-page')
    })

    return {
      avatarImg,
      nickname,
      userBio,
      bannerBg,
      currentTab,
      avatarInput,
      likedSongs,
      createdPlaylists,
      loadingPlaylists,
      favoriteAlbums,
      loadingAlbums,
      favoriteVideos,
      loadingVideos,
      currentTabTitle,
      currentTabCount,
      switchTab,
      changeAvatar,
      onAvatarChange,
      playSong,
      playAll,
      showMoreOptionsIndex,
      toggleMoreOptions,
      playNext,
      addToQueue,
      showAddToPlaylistModal,
      removeFavorite,
      goToPlaylistDetail,
      downloadAll,
      showBatchOptions,
      // 新建歌单相关
      showCreateModal,
      showCoverSelector,
      isCreating,
      newPlaylist,
      defaultCovers,
      showCreatePlaylistModal,
      closeCreatePlaylistModal,
      selectCover,
      triggerCoverUpload,
      handleCoverUpload,
      handleDragOver,
      handleDrop,
      createNewPlaylist,
      fetchUserPlaylists,
      // 封面上传相关
      coverTab,
      uploadingCover,
      uploadProgress,
      coverInput,
      // 删除歌单相关
      showDeleteModal,
      isDeleting,
      playlistToDelete,
      showDeleteConfirm,
      closeDeleteConfirm,
      confirmDeletePlaylist,
      // 批量操作相关
      showBatchModal,
      showPlaylistBatchModal,
      showAlbumBatchModal,
      showVideoBatchModal,
      selectedSongs,
      selectedPlaylists,
      selectedAlbums,
      selectedVideos,
      isSelectMode,
      toggleSelectMode,
      toggleSongSelection,
      togglePlaylistSelection,
      toggleAlbumSelection,
      toggleVideoSelection,
      selectAllSongs,
      selectAllPlaylists,
      selectAllAlbums,
      selectAllVideos,
      batchDeleteSongs,
      batchDeletePlaylists,
      batchRemoveAlbums,
      batchRemoveVideos,
      closeBatchModal,
      // 下载相关
      showDownloadModal,
      downloadSongs_list,
      isDownloading,
      downloadProgress,
      currentDownloadIndex,
      downloadResults,
      downloadSong,
      showDownloadConfirm,
      closeDownloadModal,
      confirmDownloadSongs,
      isSongDownloadable,
      // 专辑和视频相关
      loadFavoriteAlbums,
      loadFavoriteVideos,
      goToAlbumDetail,
      goToVideoDetail,
      playAlbum,
      playVideo,
      formatDate,
      formatViews,
      formatDuration
    }
  }
}
</script>

<style scoped>
/* 基础样式 */
.user-music-page {
  min-height: 100vh;
  background-color: var(--background);
  color: var(--text-primary);
}

.main-content {
  max-width: 1400px;
  margin: 0 auto;
  padding-bottom: 80px;
}

/* 用户信息横幅 */
.user-banner {
  height: 400px;
  position: relative;
  overflow: hidden;
  margin-bottom: 0;
  background-color: var(--background-card);
}

.banner-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  filter: brightness(0.7);
}

.user-profile-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  box-sizing: border-box;
  z-index: 10;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

.user-main-info {
  display: flex;
  justify-content: center;
  margin-bottom: 0;
}

.user-avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.user-avatar-container {
  position: relative;
  margin-bottom: 15px;
}

.user-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 4px solid white;
  cursor: pointer;
  transition: transform 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.user-avatar:hover {
  transform: scale(1.05);
}

.user-name-container {
  text-align: center;
}

.user-name {
  font-size: 28px;
  font-weight: bold;
  margin: 0;
  color: white;
}

/* 背景更换按钮 */
.background-changer {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 10;
}

.bg-change-btn {
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  padding: 8px 12px;
  border-radius: 20px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  border: none;
  transition: background-color 0.3s ease;
}

.bg-change-btn:hover {
  background-color: rgba(0, 0, 0, 0.7);
}

.bg-icon {
  font-size: 18px;
}

/* 内容导航标签 */
.content-tabs {
  display: flex;
  gap: 40px;
  justify-content: center;
  position: absolute;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  width: auto;
}

.tab-item {
  padding: 8px 0;
  font-size: 16px;
  cursor: pointer;
  position: relative;
  color: rgba(255, 255, 255, 0.7);
  transition: all 0.3s ease;
  border-bottom: 2px solid transparent;
}

.tab-item:hover {
  color: white;
}

.tab-item.active {
  color: white;
  font-weight: 500;
  border-bottom-color: #4ade80;
}


/* 内容区域 */
.content-area {
  background-color: var(--background-card);
  border-radius: 0;
  box-shadow: none;
  padding: var(--space-lg);
  margin-top: 0;
  border-top: 1px solid var(--border-color);
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid var(--border-color-light);
}

.content-title {
  display: flex;
  align-items: center;
}

.content-title h2 {
  font-size: 18px;
  margin-right: 10px;
  color: var(--text-color);
}

.count {
  color: var(--text-color-light);
  font-size: 14px;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.btn {
  padding: 6px 16px;
  border-radius: 30px;
  font-size: 14px;
  cursor: pointer;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn .play-icon-svg {
  width: 12px;
  height: 12px;
  margin-right: 6px;
}

.btn-primary {
  background-color: var(--primary);
  color: white;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  background-color: var(--primary-dark);
}

.btn-secondary {
  background-color: var(--background-card);
  color: var(--text-primary);
  border: 1px solid var(--border);
  transition: all 0.3s ease;
}

.btn-secondary:hover {
  background-color: var(--primary);
  color: white;
  border-color: var(--primary);
}

.icon {
  margin-right: 6px;
}

.play-icon::before {
  content: '▶';
  font-size: 14px;
  margin-right: 6px;
}

.add-icon::before {
  content: '+';
  font-size: 14px;
  margin-right: 6px;
}

.download-icon::before {
  content: '↓';
  font-size: 14px;
  margin-right: 6px;
}

.batch-icon::before {
  content: '☰';
  font-size: 14px;
  margin-right: 6px;
}

/* 歌曲列表 */
.song-list-container {
  width: 100%;
  position: relative;
  overflow: visible;
}

.song-table-header {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px solid var(--border-color-light);
  color: var(--text-color-light);
  font-size: 14px;
  background-color: var(--card-bg);
}

.song-table-body {
  /* 禁用滚动效果，显示所有内容 */
  max-height: none;
  overflow: visible;
  overflow-x: hidden;
  overflow-y: visible;
  position: relative;
}

.song-row {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid var(--border-color-light);
  transition: background-color 0.2s;
  position: relative;
}

.song-row:hover {
  background-color: var(--row-hover-bg);
}

.table-col {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding: 0 8px;
  display: flex;
  align-items: center;
}

.col-index {
  flex: 0 0 60px;
  text-align: center;
  color: var(--text-color-light);
  font-size: 14px;
  justify-content: center;
  font-weight: 500;
  position: relative;
}

.col-index::before {
  content: '';
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: -1;
}

.song-row:hover .col-index::before {
  opacity: 0.1;
}

.song-row:hover .col-index {
  color: var(--primary);
  font-weight: 600;
}

.col-song {
  flex: 2;
  min-width: 250px;
  padding-left: 0;
}

.col-artist {
  flex: 1;
  min-width: 150px;
  color: var(--text-color-lighter);
}

.col-album {
  flex: 1;
  min-width: 150px;
  color: var(--text-color-lighter);
}

.col-duration {
  flex: 0 0 100px;
  text-align: right;
  color: var(--text-color-light);
  justify-content: flex-end;
}

.col-actions {
  flex: 0 0 80px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.song-info {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 0;
}

.song-name {
  font-size: 14px;
  font-weight: 400;
  color: var(--text-color);
  text-align: left;
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
}

.tag {
  display: inline-block;
  font-size: 10px;
  padding: 1px 4px;
  border-radius: 2px;
  margin-left: 6px;
  vertical-align: middle;
}

.mv-tag {
  background-color: var(--mv-tag-bg);
  color: white;
}

.vip-tag {
  background-color: var(--vip-tag-bg);
  color: white;
}

.action-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: transparent;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0.6;
  transition: all 0.2s;
  color: var(--text-color-light);
}

.song-row:hover .action-btn {
  opacity: 1;
}

.action-btn:hover {
  background-color: var(--secondary-color);
  color: var(--text-color);
}

.play-btn .play-icon::before {
  margin-right: 0;
  font-size: 16px;
}

.download-btn {
  color: var(--success);
}

.download-btn:hover {
  background-color: var(--success);
  color: white;
}

.more-icon::before {
  content: '⋯';
  font-size: 18px;
}

/* 更多选项下拉菜单 */
.more-options-container {
  position: relative;
}

.more-options-dropdown {
  position: fixed;
  background: white;
  border: 2px solid var(--primary);
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.4), 0 0 0 1px rgba(255, 255, 255, 0.1);
  z-index: 99999;
  min-width: 180px;
  padding: 12px 0;
  animation: dropdownSlideIn 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(20px);
}

@keyframes dropdownSlideIn {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.option-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  border-radius: 8px;
  margin: 2px 8px;
}

.option-item:hover {
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%);
  color: white;
  transform: translateX(4px);
}

.option-icon {
  margin-right: 12px;
  font-size: 16px;
  width: 20px;
  text-align: center;
  transition: all 0.3s ease;
  font-weight: bold;
}

.play-next-icon {
  color: #4f46e5;
}

.queue-icon {
  color: #059669;
}

.download-icon {
  color: #0891b2;
}

.playlist-icon {
  color: #d97706;
}

.remove-icon {
  color: #dc2626;
}

.option-item:hover .option-icon {
  color: white;
  transform: scale(1.1);
}

/* 歌单列表 */
.playlist-list-container {
  width: 100%;
}

.playlist-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 20px;
}

/* 新建歌单按钮样式 */
.create-playlist-card {
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border: 2px dashed var(--border);
  background-color: var(--background-card);
}

.create-playlist-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--card-hover-shadow);
  border-color: var(--primary);
}

.create-playlist-cover {
  position: relative;
  width: 100%;
  aspect-ratio: 1;
  border-radius: 0px;
  overflow: hidden;
  margin-bottom: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.create-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

.plus-icon {
  font-size: 48px;
  color: white;
  font-weight: 300;
}

.playlist-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  position: relative;
}

.playlist-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--card-hover-shadow);
}

/* 歌单可点击区域 */
.playlist-cover,
.playlist-info {
  cursor: pointer;
}

/* 歌单操作按钮 */
.playlist-actions {
  position: absolute;
  top: 8px;
  right: 8px;
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 10;
}

.playlist-card:hover .playlist-actions {
  opacity: 1;
}

.action-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  font-size: 18px;
  font-weight: bold;
}

.delete-btn {
  background: rgba(239, 68, 68, 0.9);
  color: white;
  backdrop-filter: blur(8px);
}

.delete-btn:hover {
  background: rgba(220, 38, 38, 1);
  transform: scale(1.1);
}

.delete-icon {
  font-size: 20px;
  line-height: 1;
}

.playlist-cover {
  position: relative;
  width: 100%;
  aspect-ratio: 1;
  border-radius: 0px;
  overflow: hidden;
  margin-bottom: 10px;
}

.playlist-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.playlist-card:hover .playlist-cover img {
  transform: scale(1.05);
}

.play-count-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  padding: 10px;
  background: var(--overlay-gradient);
  color: white;
  font-size: 12px;
  display: flex;
  align-items: center;
  box-sizing: border-box;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.playlist-card:hover .play-count-overlay {
  opacity: 1;
}

.playlist-info {
  padding: 0 5px;
}

.playlist-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: var(--text-color);
}

.playlist-desc {
  font-size: 12px;
  color: var(--text-color-light);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 空状态 */
.empty-state {
  padding: 60px 0;
  text-align: center;
  color: var(--text-color-light);
}

/* 加载状态 */
.loading-playlists {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px;
  color: var(--text-secondary);
  font-size: 16px;
  gap: 16px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid var(--border);
  border-top: 4px solid var(--primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 空歌单状态 */
.empty-playlists {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px;
  color: var(--text-secondary);
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.6;
}

.empty-playlists p {
  margin: 8px 0;
  line-height: 1.5;
}

.empty-playlists p:first-of-type {
  font-size: 18px;
  font-weight: 500;
  color: var(--text-primary);
}

.empty-playlists p:last-of-type {
  font-size: 14px;
  color: var(--text-secondary);
}



/* 响应式设计 */
@media (max-width: 1200px) {
  .main-content {
    padding: 0 20px 80px;
  }
}

@media (max-width: 992px) {
  .col-index {
    flex: 0 0 50px;
  }
  .col-song {
    flex: 2;
    min-width: 180px;
  }
  .col-artist {
    flex: 1;
    min-width: 120px;
  }
  .col-album {
    flex: 1;
    min-width: 120px;
  }
  .col-duration {
    flex: 0 0 80px;
  }
  .col-actions {
    flex: 0 0 60px;
  }
}

  @media (max-width: 768px) {
  .content-tabs {
    bottom: 20px;
    gap: 25px;
    overflow-x: auto;
    padding: 0 20px;
  }
  .tab-item {
    padding: 8px 0;
    white-space: nowrap;
    font-size: 14px;
  }
  .user-banner {
    height: 350px;
  }
  .user-main-info {
    margin-bottom: 0;
  }
  .user-avatar-section {
    align-items: center;
  }
  .user-avatar {
    width: 100px;
    height: 100px;
  }
  .user-name {
    font-size: 22px;
  }
  .content-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  .action-buttons {
    flex-wrap: wrap;
    width: 100%;
  }
  .btn {
    flex: 1;
    min-width: 120px;
  }
}

@media (max-width: 576px) {
  .col-index {
    flex: 0 0 40px;
    font-size: 12px;
  }
  .col-song {
    flex: 2;
    min-width: 120px;
  }
  .col-artist {
    display: none;
  }
  .col-album {
    flex: 1;
    min-width: 100px;
    font-size: 12px;
  }
  .col-duration {
    flex: 0 0 60px;
    font-size: 12px;
  }
  .col-actions {
    flex: 0 0 50px;
  }
  
  .user-banner {
    height: 300px;
  }
  .user-avatar {
    width: 80px;
    height: 80px;
  }
  .user-name {
    font-size: 18px;
  }
  .user-stats {
    flex-direction: row;
    gap: 15px;
  }
}
  



/* 黑色主题特殊调整 */
[data-theme="black"] .content-area {
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

[data-theme="black"] .song-table-header {
  background-color: var(--row-hover-bg);
}

/* 新建歌单模态框样式 */
.create-playlist-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  backdrop-filter: blur(4px);
}

/* 封面选择标签页 */
.cover-tabs {
  display: flex;
  margin-bottom: 16px;
  border-bottom: 1px solid var(--border);
}

.tab-btn {
  flex: 1;
  padding: 8px 16px;
  border: none;
  background: transparent;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
  border-bottom: 2px solid transparent;
}

.tab-btn:hover {
  color: var(--text-primary);
}

.tab-btn.active {
  color: var(--primary);
  border-bottom-color: var(--primary);
}

/* 上传区域样式 */
.upload-area {
  padding: 16px 0;
}

.upload-zone {
  border: 2px dashed var(--border);
  border-radius: 8px;
  padding: 32px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  background: var(--background-light);
}

.upload-zone:hover {
  border-color: var(--primary);
  background: var(--background-card);
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.upload-icon {
  font-size: 48px;
  opacity: 0.6;
}

.upload-text {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.upload-hint {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

/* 上传进度样式 */
.upload-progress {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.progress-bar {
  width: 100%;
  max-width: 200px;
  height: 4px;
  background: var(--background-card);
  border-radius: 2px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--primary), var(--primary-light));
  border-radius: 2px;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 14px;
  color: var(--text-secondary);
}

/* 封面操作按钮 */
.current-cover {
  position: relative;
  overflow: hidden;
}

.cover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
}

.current-cover:hover .cover-overlay {
  opacity: 1;
}

.cover-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  background: rgba(255, 255, 255, 0.9);
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  font-size: 18px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-btn:hover {
  background: white;
  transform: scale(1.1);
}

/* 自定义封面样式 */
.custom-cover {
  position: relative;
}

.custom-badge {
  position: absolute;
  top: 4px;
  right: 4px;
  background: var(--primary);
  color: white;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 10px;
  font-weight: 600;
}

.create-modal-content {
  background-color: var(--background-card);
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
  color: var(--text-primary);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid var(--border);
}

.modal-header h3 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: var(--text-primary);
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: var(--text-secondary);
  padding: 0;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.close-btn:hover {
  background-color: var(--background);
  color: var(--text-primary);
}

.create-form {
  padding: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: var(--text-primary);
  font-size: 14px;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid var(--border);
  border-radius: 8px;
  font-size: 14px;
  background-color: var(--background);
  color: var(--text-primary);
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.char-count {
  text-align: right;
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 4px;
}

.cover-selector {
  position: relative;
}

.current-cover {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid var(--border);
  transition: border-color 0.2s;
}

.current-cover:hover {
  border-color: var(--primary);
}

.current-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.placeholder-cover {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
  color: var(--text-secondary);
}

.image-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.placeholder-cover span {
  font-size: 12px;
}

.cover-options {
  position: absolute;
  top: 130px;
  left: 0;
  background-color: var(--background-card);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  z-index: 10;
  min-width: 300px;
}

.cover-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
  margin-bottom: 12px;
}

.cover-option {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.2s;
}

.cover-option:hover {
  border-color: var(--primary);
  transform: scale(1.05);
}

.cover-option.active {
  border-color: var(--primary);
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
}

.cover-option img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-done-btn {
  width: 100%;
  padding: 8px;
  background-color: var(--primary);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.cover-done-btn:hover {
  background-color: var(--primary-dark);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid var(--border);
}

.modal-footer .btn {
  min-width: 80px;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn:disabled:hover {
  background-color: initial;
  color: initial;
  border-color: initial;
}

/* 删除确认对话框样式 */
.delete-confirm-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2001;
  backdrop-filter: blur(4px);
}

.delete-modal-content {
  background-color: var(--background-card);
  border-radius: 12px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
  color: var(--text-primary);
}

.delete-content {
  padding: 24px;
  text-align: center;
}

.warning-icon {
  font-size: 48px;
  margin-bottom: 16px;
  color: #f59e0b;
}

.warning-text {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
  line-height: 1.5;
}

.warning-desc {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
  line-height: 1.5;
}

.btn-danger {
  background-color: #ef4444;
  color: white;
  border: 1px solid #ef4444;
}

.btn-danger:hover {
  background-color: #dc2626;
  border-color: #dc2626;
}

.btn-danger:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-danger:disabled:hover {
  background-color: #ef4444;
  border-color: #ef4444;
}

/* 批量操作模态框样式 */
.batch-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  backdrop-filter: blur(4px);
}

.batch-modal-content {
  background: var(--background-card);
  border-radius: var(--border-radius-lg);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  max-width: 600px;
  width: 90%;
  max-height: 80vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.batch-content {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.batch-header {
  padding: var(--space-md) var(--space-lg);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--background);
}

.select-info {
  font-size: 14px;
  color: var(--text-secondary);
  font-weight: 500;
}

.batch-list {
  flex: 1;
  overflow-y: auto;
  padding: var(--space-sm);
  max-height: 400px;
}

.batch-item {
  margin-bottom: var(--space-xs);
}

.checkbox-wrapper {
  display: flex;
  align-items: center;
  padding: var(--space-md);
  border-radius: var(--border-radius);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.checkbox-wrapper:hover {
  background: var(--background);
  border-color: var(--border-color);
}

.checkbox-wrapper input[type="checkbox"] {
  margin-right: var(--space-md);
  transform: scale(1.2);
  accent-color: var(--primary);
}

.song-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.song-name {
  font-weight: 500;
  color: var(--text-primary);
}

.song-artist {
  font-size: 12px;
  color: var(--text-secondary);
}

.playlist-info {
  display: flex;
  align-items: center;
  gap: var(--space-md);
}

.playlist-cover-small {
  width: 40px;
  height: 40px;
  border-radius: var(--border-radius);
  object-fit: cover;
}

.playlist-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.playlist-name {
  font-weight: 500;
  color: var(--text-primary);
}

.playlist-desc {
  font-size: 12px;
  color: var(--text-secondary);
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 下载模态框样式 */
.download-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(8px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  animation: luxuryFadeIn 0.4s ease-out;
}

.download-modal-content {
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 24px;
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  overflow: hidden;
  box-shadow: 
    0 8px 32px rgba(31, 38, 135, 0.37),
    inset 0 1px 0 rgba(255, 255, 255, 0.3),
    0 40px 80px rgba(0, 0, 0, 0.3);
  animation: luxurySlideUp 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.download-modal-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, 
    transparent, 
    rgba(255, 255, 255, 0.5), 
    transparent);
}

.download-content {
  padding: 30px;
  color: white;
  max-height: calc(80vh - 120px);
  overflow-y: auto;
}

.download-info {
  margin-bottom: 24px;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  animation: luxurySlideUp 0.4s ease-out;
  animation-delay: 0.1s;
  animation-fill-mode: both;
}

.info-item:nth-child(2) {
  animation-delay: 0.2s;
}

.info-item:nth-child(3) {
  animation-delay: 0.3s;
}

.info-item:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(5px);
  border-color: rgba(255, 255, 255, 0.2);
}

.info-icon {
  margin-right: 12px;
  font-size: 18px;
  filter: drop-shadow(0 0 8px rgba(255, 255, 255, 0.5));
}

/* 下载进度条 */
.download-progress {
  margin: 30px 0;
  padding: 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  animation: luxurySlideUp 0.5s ease-out;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-size: 14px;
  color: white;
  font-weight: 600;
}

.progress-info span:first-child {
  animation: downloadingPulse 2s ease-in-out infinite;
}

.progress-info span:last-child {
  font-size: 16px;
  font-weight: 700;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: percentGlow 2s ease-in-out infinite alternate;
}

.progress-bar {
  width: 100%;
  height: 12px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  overflow: hidden;
  position: relative;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.2);
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, 
    #4facfe 0%, 
    #00f2fe 25%,
    #667eea 50%,
    #764ba2 75%,
    #f093fb 100%);
  background-size: 200% 100%;
  border-radius: 8px;
  transition: width 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  animation: rainbowFlow 3s ease-in-out infinite;
}

.progress-fill::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, 
    transparent, 
    rgba(255, 255, 255, 0.4), 
    transparent);
  animation: luxuryProgressShine 2s ease-in-out infinite;
}

.download-status {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
}

.download-status span {
  padding: 4px 8px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 6px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

/* 歌曲列表 */
.download-song-list {
  margin-top: 20px;
}

.list-header h4 {
  margin: 0 0 15px 0;
  color: white;
  font-size: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.song-list {
  max-height: 200px;
  overflow-y: auto;
}

.download-song-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  margin-bottom: 8px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.download-song-item:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(5px);
}

.download-song-item .song-info {
  flex: 1;
}

.download-song-item .song-name {
  display: block;
  font-weight: 500;
  color: white;
  margin-bottom: 4px;
}

.download-song-item .song-artist {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
}

.file-status {
  margin-left: 10px;
}

.status-icon {
  font-size: 16px;
  font-weight: bold;
  padding: 4px 8px;
  border-radius: 6px;
}

.status-icon.ready {
  color: #10b981;
  background: rgba(16, 185, 129, 0.2);
  border: 1px solid rgba(16, 185, 129, 0.3);
}

.status-icon.unavailable {
  color: #ef4444;
  background: rgba(239, 68, 68, 0.2);
  border: 1px solid rgba(239, 68, 68, 0.3);
}

.more-songs {
  text-align: center;
  padding: 10px;
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
  font-style: italic;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

/* 隐藏滚动条样式 */
/* 隐藏所有滚动条但保持滚动功能 */
::-webkit-scrollbar {
  width: 0px;
  height: 0px;
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: transparent;
}

::-webkit-scrollbar-track {
  background: transparent;
}

/* 为Firefox浏览器隐藏滚动条 */
* {
  scrollbar-width: none;
  -ms-overflow-style: none;
}

/* 为IE浏览器隐藏滚动条 */
* {
  -ms-overflow-style: none;
}

/* 确保页面和容器的滚动条都被隐藏 */
body, html {
  overflow-x: hidden;
}

.user-music-page {
  overflow-x: hidden;
}

.song-table-body {
  /* 不需要隐藏滚动条，因为已经禁用了滚动 */
  overflow: visible;
}

.batch-list {
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.batch-list::-webkit-scrollbar {
  width: 0px;
  background: transparent;
}

.download-content {
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.download-content::-webkit-scrollbar {
  width: 0px;
  background: transparent;
}

.song-list {
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.song-list::-webkit-scrollbar {
  width: 0px;
  background: transparent;
}

/* 标签页数量样式 */
.tab-count {
  margin-left: 6px;
  font-size: 12px;
  color: var(--text-secondary);
  font-weight: normal;
}

.tab-item.active .tab-count {
  color: var(--primary);
}

/* 专辑列表样式 */
.album-list-container {
  width: 100%;
}

.loading-albums {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  gap: 16px;
  color: var(--text-secondary);
}

.albums-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
  padding: 20px 0;
}

.album-card {
  background: var(--background-card);
  border-radius: 0px;
  overflow: hidden;
  transition: box-shadow 0.3s ease;
  cursor: pointer;
}

.album-card:hover {
  box-shadow: var(--card-hover-shadow);
}

.album-cover {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.album-cover .cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.album-card:hover .cover-image {
  transform: scale(1.05);
}

.album-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.album-card:hover .album-overlay {
  opacity: 1;
}

.play-album-btn {
  width: 50px;
  height: 50px;
  border: none;
  border-radius: 50%;
  background: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.play-album-btn:hover {
  transform: scale(1.1);
}

.play-album-btn .play-icon-img {
  width: 20px;
  height: 20px;
  filter: brightness(0) invert(1);
}

.album-info {
  padding: 16px;
}

.album-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.album-artist {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0 0 4px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.album-date {
  font-size: 12px;
  color: var(--text-tertiary);
  margin: 0;
}

.empty-albums {
  text-align: center;
  padding: 60px 20px;
  color: var(--text-secondary);
}

.empty-albums .empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-albums p {
  margin: 8px 0;
  font-size: 14px;
}

/* 视频列表样式 */
.video-list-container {
  width: 100%;
}

.loading-videos {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  gap: 16px;
  color: var(--text-secondary);
}

.videos-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  padding: 20px 0;
}

.video-card {
  background: var(--background-card);
  border-radius: 0px;
  overflow: hidden;
  transition: box-shadow 0.3s ease;
  cursor: pointer;
}

.video-card:hover {
  box-shadow: var(--card-hover-shadow);
}

.video-cover {
  position: relative;
  width: 100%;
  height: 160px;
  overflow: hidden;
}

.video-cover .cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.video-card:hover .cover-image {
  transform: scale(1.05);
}

.video-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.video-card:hover .video-overlay {
  opacity: 1;
}

.play-video-btn {
  width: 50px;
  height: 50px;
  border: none;
  border-radius: 50%;
  background: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.play-video-btn:hover {
  transform: scale(1.1);
}

.play-video-btn .play-icon-svg {
  width: 20px;
  height: 20px;
  fill: white;
}

.video-duration {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.video-info {
  padding: 16px;
}

.video-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.video-artist {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0 0 4px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.video-views {
  font-size: 12px;
  color: var(--text-tertiary);
  margin: 0;
}

.empty-videos {
  text-align: center;
  padding: 60px 20px;
  color: var(--text-secondary);
}

.empty-videos .empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-videos p {
  margin: 8px 0;
  font-size: 14px;
}
</style>
