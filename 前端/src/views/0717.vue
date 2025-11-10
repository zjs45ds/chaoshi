// 0717.vue
<template>
  <div class="xue-theme-page">
    <!-- 顶部英雄区域 -->
    <div class="hero-section">
      <div class="hero-background" :style="heroBackgroundStyle"></div>
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <a href="https://www.douyin.com/user/MS4wLjABAAAAV9gNIy6uaO24sU0-g3Uz_kxHqIRYQrKqDXtLSBtsm5E?from_tab_name=main" target="_blank" class="avatar-link">
          <div class="hero-avatar-container">
            <div class="hero-avatar"></div>
          </div>
        </a>
        <a href="https://weibo.com/xuezhiqian" target="_blank" class="name-link">
          <BoxReveal :duration="0.6" :delay="0.08" :blur="'10px'" :yOffset="20">
            <h1 class="hero-title">薛之谦</h1>
          </BoxReveal>
        </a>
        <p class="hero-subtitle">音乐才子 · 创作歌手 · 演员</p>
        <div class="hero-badge">
          <span><LetterPullup :words="'出道20周年'" :delay="0.08" /></span>
          <span class="celebration-icon">🎉</span>
        </div>
        <Marquee 
          :pause-on-hover="true"
          :line1-duration="30"
          :line2-duration="45"
        />
      </div>
    </div>

    <!-- 人物简介 -->
    <div class="profile-section">
      <div class="container">
        <h2 class="section-title">
          <BoxReveal :duration="0.6" :delay="0.08" :blur="'10px'" :yOffset="20">
            <span>人物简介</span>
          </BoxReveal>
        </h2>
        <div class="profile-card">
          <div class="profile-content-wrapper">
            <LetterPullup 
              :words="'薛之谦(Joker Xue)，1983年7月17日出生于上海，毕业于格里昂酒店管理学院，华语流行乐男歌手、影视演员、音乐制作人。2005年，因参加选秀节目《我型我秀》而正式出道。2006年6月12日，发行个人首张同名专辑《薛之谦》，随后凭借歌曲《认真的雪》获得广泛关注。2007年，发行个人第二张专辑《你过得好吗》，该专辑首月销量突破15万。薛之谦以其独特的音乐风格和真挚的歌词创作而备受喜爱。他的音乐作品融合了流行、摇滚、民谣等多种元素，擅长用音乐表达生活中的情感和感悟。除了音乐事业，薛之谦还活跃于影视和综艺领域，展现了多方面的才华。'"
              :delay="0.05"
              class="profile-animated-text"
            />
          </div>
        </div>
      </div>
    </div>

    <div class="personal-info-section">
      <div class="container">
        <h2 class="section-title">
          <BoxReveal :duration="0.6" :delay="0.08" :blur="'10px'" :yOffset="20">
            <span>个人档案</span>
          </BoxReveal>
        </h2>
        <div class="info-grid">
          <div class="info-item">
            <span class="info-label"><LetterPullup :words="'出生日期'" :delay="0.08" /></span>
            <span class="info-value"><LetterPullup :words="'1983年7月17日'" :delay="0.06" /></span>
          </div>
          <div class="info-item">
            <span class="info-label"><LetterPullup :words="'籍贯'" :delay="0.08" /></span>
            <span class="info-value"><LetterPullup :words="'上海市'" :delay="0.06" /></span>
          </div>
          <div class="info-item">
            <span class="info-label"><LetterPullup :words="'星座'" :delay="0.08" /></span>
            <span class="info-value"><LetterPullup :words="'巨蟹座'" :delay="0.06" /></span>
          </div>
          <div class="info-item">
            <span class="info-label"><LetterPullup :words="'身高'" :delay="0.08" /></span>
            <span class="info-value"><LetterPullup :words="'172cm'" :delay="0.06" /></span>
          </div>
          <div class="info-item">
            <span class="info-label"><LetterPullup :words="'体重'" :delay="0.08" /></span>
            <span class="info-value"><LetterPullup :words="'55kg'" :delay="0.06" /></span>
          </div>
          <div class="info-item">
            <span class="info-label"><LetterPullup :words="'血型'" :delay="0.08" /></span>
            <span class="info-value"><LetterPullup :words="'O型'" :delay="0.06" /></span>
          </div>
          <div class="info-item">
            <span class="info-label"><LetterPullup :words="'昵称'" :delay="0.08" /></span>
            <span class="info-value"><LetterPullup :words="'谦谦、薛老板'" :delay="0.06" /></span>
          </div>
          <div class="info-item">
            <span class="info-label"><LetterPullup :words="'特长'" :delay="0.08" /></span>
            <span class="info-value"><LetterPullup :words="'唱歌、创作、主持'" :delay="0.06" /></span>
          </div>
          <div class="info-item">
            <span class="info-label"><LetterPullup :words="'爱好'" :delay="0.08" /></span>
            <span class="info-value"><LetterPullup :words="'音乐、喜剧、火锅'" :delay="0.06" /></span>
          </div>
          <div class="info-item">
            <span class="info-label"><LetterPullup :words="'性格'" :delay="0.08" /></span>
            <span class="info-value"><LetterPullup :words="'幽默、感性、执着'" :delay="0.06" /></span>
          </div>
          <div class="info-item">
            <span class="info-label"><LetterPullup :words="'座右铭'" :delay="0.08" /></span>
            <span class="info-value"><LetterPullup :words="'笑对人生，音乐为伴'" :delay="0.06" /></span>
          </div>
          <div class="info-item">
            <span class="info-label"><LetterPullup :words="'经纪公司'" :delay="0.08" /></span>
            <span class="info-value"><LetterPullup :words="'海蝶音乐'" :delay="0.06" /></span>
          </div>
        </div>
      </div>
    </div>

    <!-- 音乐生涯时间轴 -->
    <div class="career-section">
      <div class="container">
        <h2 class="section-title">
          <BoxReveal :duration="0.6" :delay="0.08" :blur="'10px'" :yOffset="20">
            <span>音乐生涯</span>
          </BoxReveal>
        </h2>
        <div class="timeline">
          <div class="timeline-item">
            <div class="timeline-date">2005年</div>
            <div class="timeline-content">
              <h3>出道</h3>
              <p><LetterPullup :words="'参加东方卫视《我型我秀》比赛，获得全国四强，正式进入演艺圈。12月发行首张同名专辑《薛之谦》，主打歌《认真的雪》红遍大江南北，成为其代表作之一。'" :delay="0.03" /></p>
            </div>
          </div>
          <div class="timeline-item">
            <div class="timeline-date">2006年</div>
            <div class="timeline-content">
              <h3>第二张专辑</h3>
              <p><LetterPullup :words="'发行第二张专辑《薛之谦》（同名专辑改版），收录《黄色枫叶》《王子归来》等歌曲。获得雪碧中国原创音乐流行榜最受欢迎新人奖。'" :delay="0.03" /></p>
            </div>
          </div>
          <div class="timeline-item">
            <div class="timeline-date">2007年</div>
            <div class="timeline-content">
              <h3>第三张专辑</h3>
              <p><LetterPullup :words="'发行第三张专辑《你过得好吗》，同名主打歌《你过得好吗》获得多个音乐奖项。'" :delay="0.03" /></p>
            </div>
          </div>
          <div class="timeline-item">
            <div class="timeline-date">2008年</div>
            <div class="timeline-content">
              <h3>第四张专辑</h3>
              <p><LetterPullup :words="'发行第四张专辑《深深爱过你》，尝试抒情和摇滚两种风格，同名主打歌《深深爱过你》获得广泛好评。同年，在上海举行个人首场演唱会「谦年传说」。'" :delay="0.03" /></p>
            </div>
          </div>
          <div class="timeline-item">
            <div class="timeline-date">2012年</div>
            <div class="timeline-content">
              <h3>独立发展</h3>
              <p><LetterPullup :words="'成立个人工作室，开始独立音乐创作与制作。发行专辑《几个薛之谦》，包办了专辑中大部分歌曲的词曲创作。'" :delay="0.03" /></p>
            </div>
          </div>
          <div class="timeline-item">
            <div class="timeline-date">2013年</div>
            <div class="timeline-content">
              <h3>第五张专辑</h3>
              <p><LetterPullup :words="'发行个人第五张专辑《意外》。'" :delay="0.03" /></p>
            </div>
          </div>
          <div class="timeline-item">
            <div class="timeline-date">2014年</div>
            <div class="timeline-content">
              <h3>获得奖项</h3>
              <p><LetterPullup :words="'获得第21届东方风云榜最佳唱作人奖、音悦V榜年度盛典年度创作歌手奖。'" :delay="0.03" /></p>
            </div>
          </div>
          <div class="timeline-item">
            <div class="timeline-date">2015年</div>
            <div class="timeline-content">
              <h3>事业转机</h3>
              <p><LetterPullup :words="'首度担当制作人，并发行原创EP《绅士》；同年，主演都市励志剧《妈妈像花儿一样》。凭借《演员》再度受到关注，歌曲在各大音乐平台排行榜上取得优异成绩。'" :delay="0.03" /></p>
            </div>
          </div>
          <div class="timeline-item">
            <div class="timeline-date">2016年</div>
            <div class="timeline-content">
              <h3>事业巅峰</h3>
              <p><LetterPullup :words="'发行专辑《初学者》，收录《丑八怪》《刚刚好》等热门歌曲，横扫各大音乐榜单。获得第16届全球华语歌曲排行榜最受欢迎男歌手、五大最受欢迎男歌手奖、上海地区杰出歌手奖及最受欢迎创作歌手奖，而歌曲《初学者》则获得年度二十大金曲奖。'" :delay="0.03" /></p>
            </div>
          </div>
          <div class="timeline-item">
            <div class="timeline-date">2017年</div>
            <div class="timeline-content">
              <h3>持续突破</h3>
              <p><LetterPullup :words="'开启「我好像在哪见过你」全国巡回演唱会；同年，获得第7届全球流行音乐年度盛典年度最佳男歌手、MusicRadio榜中国TOP排行榜内地最佳男歌手。发行专辑《渡 The Crossing》，尝试电子音乐风格。'" :delay="0.03" /></p>
            </div>
          </div>
          <div class="timeline-item">
            <div class="timeline-date">2018年</div>
            <div class="timeline-content">
              <h3>音乐创新</h3>
              <p><LetterPullup :words="'薛之谦「摩天大楼」世界巡回演唱会于北京站启程，整个巡演横跨4大洲，21个城市，累计上演场次23场。发行专辑《怪咖》，继续音乐风格的探索与创新。'" :delay="0.03" /></p>
            </div>
          </div>
          <div class="timeline-item">
            <div class="timeline-date">2019年</div>
            <div class="timeline-content">
              <h3>获得奖项</h3>
              <p><LetterPullup :words="'获得2019华人歌曲音乐盛典内地年度最受欢迎歌手奖。'" :delay="0.03" /></p>
            </div>
          </div>
          <div class="timeline-item">
            <div class="timeline-date">2020年</div>
            <div class="timeline-content">
              <h3>音乐探索</h3>
              <div class="timeline-subitem">
                <div class="timeline-subdate">7月</div>
                <p><LetterPullup :words="'发行专辑《天外来物》，尝试多元音乐风格。'" :delay="0.03" /></p>
              </div>
              <div class="timeline-subitem">
                <div class="timeline-subdate">8月</div>
                <p><LetterPullup :words="'参与音乐综艺节目《明日之子乐团季》担任星推官。'" :delay="0.03" /></p>
              </div>
            </div>
          </div>
          <div class="timeline-item">
            <div class="timeline-date">2021年</div>
            <div class="timeline-content">
              <h3>持续创作</h3>
              <div class="timeline-subitem">
                <div class="timeline-subdate">1月</div>
                <p><LetterPullup :words="'发行单曲《变废为宝》。'" :delay="0.03" /></p>
              </div>
              <div class="timeline-subitem">
                <div class="timeline-subdate">7月</div>
                <p><LetterPullup :words="'发行单曲《耗尽》。'" :delay="0.03" /></p>
              </div>
              <div class="timeline-subitem">
                <div class="timeline-subdate">全年</div>
                <p><LetterPullup :words="'「摩天大楼」巡回演唱会继续进行，因疫情影响部分场次延期。'" :delay="0.03" /></p>
              </div>
            </div>
          </div>
          <div class="timeline-item">
            <div class="timeline-date">2022年</div>
            <div class="timeline-content">
              <h3>专辑发布</h3>
              <div class="timeline-subitem">
                <div class="timeline-subdate">9月</div>
                <p><LetterPullup :words="'发行专辑《无数》，包含《可》《男二号》等热门歌曲。'" :delay="0.03" /></p>
              </div>
              <div class="timeline-subitem">
                <div class="timeline-subdate">12月</div>
                <p><LetterPullup :words="'获得网易云音乐年度音乐奖。'" :delay="0.03" /></p>
              </div>
            </div>
          </div>
          <div class="timeline-item">
            <div class="timeline-date">2023年</div>
            <div class="timeline-content">
              <h3>巡回演出</h3>
              <div class="timeline-subitem">
                <div class="timeline-subdate">3月</div>
                <p><LetterPullup :words="'「天外来物」巡回演唱会重启并扩展，覆盖多个城市。'" :delay="0.03" /></p>
              </div>
              <div class="timeline-subitem">
                <div class="timeline-subdate">5月</div>
                <p><LetterPullup :words="'发行单曲《崇拜》。'" :delay="0.03" /></p>
              </div>
              <div class="timeline-subitem">
                <div class="timeline-subdate">10月</div>
                <p><LetterPullup :words="'发行单曲《念》。'" :delay="0.03" /></p>
              </div>
            </div>
          </div>
          <div class="timeline-item">
            <div class="timeline-date">2024年</div>
            <div class="timeline-content">
              <h3>音乐综艺与创新</h3>
              <div class="timeline-subitem">
                <div class="timeline-subdate">4月</div>
                <p><LetterPullup :words="'参与江苏卫视《音乐缘计划》，担任节目嘉宾。'" :delay="0.03" /></p>
              </div>
              <div class="timeline-subitem">
                <div class="timeline-subdate">全年</div>
                <p><LetterPullup :words="'尝试音乐风格创新，探索多元化音乐表达。'" :delay="0.03" /></p>
              </div>
            </div>
          </div>
          <div class="timeline-item">
            <div class="timeline-date">2025年</div>
            <div class="timeline-content">
              <h3>多元发展与舞台魅力</h3>
              <div class="timeline-subitem">
                <div class="timeline-subdate">5月1日</div>
                <p><LetterPullup :words="'参加南京咪豆音乐节，燃爆现场。'" :delay="0.03" /></p>
              </div>
              <div class="timeline-subitem">
                <div class="timeline-subdate">5月15日</div>
                <p><LetterPullup :words="'「天外来物」巡回演唱会重庆站开启售票，新增的「银河探秘区」虽因部分视线遮挡引发争议，但仍在几分钟内售罄，彰显超高人气。'" :delay="0.03" /></p>
              </div>
              <div class="timeline-subitem">
                <div class="timeline-subdate">7月17日</div>
                <p><LetterPullup :words="'发布生日单曲《跃》，以温暖旋律传递积极能量。'" :delay="0.03" /></p>
              </div>
              <div class="timeline-subitem">
                <div class="timeline-subdate">8月5日</div>
                <p><LetterPullup :words="'大胆尝试工业摇滚风格，发布单曲《金斧子银斧子》，展现音乐多元化探索。'" :delay="0.03" /></p>
              </div>
              <div class="timeline-subitem">
                <div class="timeline-subdate">8月16日</div>
                <p><LetterPullup :words="'官宣将参加2025年太湖湾音乐节，具体场次信息待官方进一步确认（注：目前网络信息显示存在时间和届数不一致的情况，建议以官方最新公告为准）。'" :delay="0.03" /></p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
   

<!-- 演唱会记录 -->
<div class="concert-section">
  <div class="container">
    <h2 class="section-title">
      <BoxReveal :duration="0.6" :delay="0.08" :blur="'10px'" :yOffset="20">
        <span>演唱会记录</span>
      </BoxReveal>
    </h2>
    
    
    <div class="concert-table-container">
      <table class="concert-table">
        <thead>
          <tr>
            <th>举办时间</th>
            <th>演唱会名称</th>
            <th>总场次</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr class="concert-row">
            <td>2021-10-23 至 2024-08-18</td>
            <td>薛之谦"天外来物"世界巡回演唱会</td>
            <td>{{ calculateTotal('天外来物') }}场</td>
            <td><button class="expand-btn" @click="toggleExpand('天外来物')"><img :src="'/src/assets/展开.svg'" :class="{ 'rotate-180': expandedConcert === '天外来物' }" alt="展开/收起" class="expand-icon"></button></td>
          </tr>
          <tr class="detail-row" v-show="expandedConcert === '天外来物'">
            <td colspan="4">
              <table class="detail-table">
                <thead>
                  <tr>
                    <th>分场举办时间</th>
                    <th>地点</th>
                    <th>场馆名称</th>
                    <th>场次</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>2021-10-23 至 2021-10-24</td>
                    <td>苏州</td>
                    <td>苏州体育中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2021-11-27 至 2021-11-28</td>
                    <td>广州</td>
                    <td>广州大学城体育中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2022-12-24</td>
                    <td>海口</td>
                    <td>海口五源河体育场</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2023-03-18 至 2023-03-19</td>
                    <td>衢州</td>
                    <td>衢州体育中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2023-04-01</td>
                    <td>南宁</td>
                    <td>南宁体育中心体育场</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2023-04-14 至 2023-04-15</td>
                    <td>佛山</td>
                    <td>佛山世纪莲体育中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2023-04-21 至 2023-04-22</td>
                    <td>南昌</td>
                    <td>南昌国际体育中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2023-04-28 至 2023-04-29</td>
                    <td>南京</td>
                    <td>南京奥林匹克体育中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2023-05-13 至 2023-05-14</td>
                    <td>济南</td>
                    <td>济南奥林匹克体育中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2023-05-20 至 2023-05-21</td>
                    <td>郑州</td>
                    <td>郑州奥林匹克体育中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2023-05-27</td>
                    <td>连云港</td>
                    <td>连云港体育中心体育场</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2023-06-03 至 2023-06-04</td>
                    <td>武汉</td>
                    <td>武汉体育中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2023-06-10</td>
                    <td>哈尔滨</td>
                    <td>哈尔滨国际会展中心体育场</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2023-06-21 至 2023-06-23</td>
                    <td>上海</td>
                    <td>上海虹口足球场</td>
                    <td>3场</td>
                  </tr>
                  <tr>
                    <td>2023-07-01 至 2023-07-02</td>
                    <td>西安</td>
                    <td>西安奥林匹克体育中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2023-07-08 至 2023-07-09</td>
                    <td>青岛</td>
                    <td>青岛市民健身中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2023-07-14 至 2023-07-16</td>
                    <td>衡阳</td>
                    <td>衡阳市体育中心体育场</td>
                    <td>3场</td>
                  </tr>
                  <tr>
                    <td>2023-07-21 至 2023-07-23</td>
                    <td>合肥</td>
                    <td>合肥体育中心体育场</td>
                    <td>3场</td>
                  </tr>
                  <tr>
                    <td>2023-07-28 至 2023-07-29</td>
                    <td>深圳</td>
                    <td>深圳大运中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2023-08-05 至 2023-08-06</td>
                    <td>宜昌</td>
                    <td>宜昌奥林匹克体育中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2023-08-11 至 2023-08-13</td>
                    <td>北京</td>
                    <td>国家体育场"鸟巢"</td>
                    <td>3场</td>
                  </tr>
                  <tr>
                    <td>2023-08-18 至 2023-08-19</td>
                    <td>兰州</td>
                    <td>兰州奥林匹克体育中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2023-08-26 至 2023-08-27</td>
                    <td>遵义</td>
                    <td>遵义市奥林匹克体育中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2023-09-02 至 2023-09-03</td>
                    <td>沈阳</td>
                    <td>沈阳奥林匹克体育中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2023-09-09</td>
                    <td>成都</td>
                    <td>成都东安湖体育公园主体育场</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2023-09-16 至 2023-09-17</td>
                    <td>福州</td>
                    <td>福州海峡奥林匹克体育中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2023-09-22 至 2023-09-24</td>
                    <td>广州</td>
                    <td>广州天河体育场</td>
                    <td>3场</td>
                  </tr>
                  <tr>
                    <td>2023-09-29 至 2023-09-30</td>
                    <td>滁州</td>
                    <td>滁州奥林匹克体育中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2023-10-05 至 2023-10-06</td>
                    <td>石家庄</td>
                    <td>河北奥林匹克体育中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2023-10-14 至 2023-10-15</td>
                    <td>天津</td>
                    <td>天津奥林匹克中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2023-10-20 至 2023-10-21</td>
                    <td>常州</td>
                    <td>常州奥林匹克体育中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2023-10-27 至 2023-10-28</td>
                    <td>清远</td>
                    <td>清远体育中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2023-11-03 至 2023-11-04</td>
                    <td>成都</td>
                    <td>成都东安湖体育公园主体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2023-11-11 至 2023-11-12</td>
                    <td>温州</td>
                    <td>温州奥林匹克体育中心主体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2023-11-19</td>
                    <td>伦敦</td>
                    <td>伦敦温布利体育馆（OvO Arena Wembley）</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2023-11-22</td>
                    <td>巴黎</td>
                    <td>巴黎天顶体育馆（Zenith Paris - La Vilette）</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2023-12-02 至 2023-12-03</td>
                    <td>香港</td>
                    <td>香港亚洲国际博览馆</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2023-12-08 至 2023-12-10</td>
                    <td>澳门</td>
                    <td>澳门银河综艺馆</td>
                    <td>3场</td>
                  </tr>
                  <tr>
                    <td>2023-12-16 至 2023-12-17</td>
                    <td>三明</td>
                    <td>三明体育中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2023-12-23 至 2023-12-25</td>
                    <td>吉隆坡</td>
                    <td>吉隆坡亚通体育馆（Axiata Arena）</td>
                    <td>3场</td>
                  </tr>
                  <tr>
                    <td>2024-01-05 至 2024-01-07</td>
                    <td>新加坡</td>
                    <td>新加坡室内体育馆</td>
                    <td>3场</td>
                  </tr>
                  <tr>
                    <td>2024-01-13 至 2024-01-14</td>
                    <td>曲靖</td>
                    <td>曲靖市文化体育公园体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2024-01-27</td>
                    <td>海口</td>
                    <td>海口五源河体育场</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2024-02-17</td>
                    <td>纽约</td>
                    <td>纽约巴克莱中心（Barclay Center）</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2024-02-24</td>
                    <td>拉斯维加斯</td>
                    <td>拉斯维加斯米高梅花园剧场（MGM Grand Garden Arena）</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2024-03-04</td>
                    <td>圣荷西</td>
                    <td>圣荷西SAP中心（SAP Center）</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2024-03-07</td>
                    <td>温哥华</td>
                    <td>温哥华罗渣士体育馆（Rogers Arena）</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2024-03-13</td>
                    <td>多伦多</td>
                    <td>多伦多可口可乐竞技场（Coca-Cola Coliseum）</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2024-03-23</td>
                    <td>悉尼</td>
                    <td>悉尼库多斯银行体育馆（Qudos Bank Arena）</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2024-03-26</td>
                    <td>墨尔本</td>
                    <td>墨尔本罗德·拉沃尔球场（Rod Laver Arena）</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2024-03-30</td>
                    <td>奥克兰</td>
                    <td>奥克兰火花竞技场（Spark Arena）</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2024-04-06 至 2024-04-07</td>
                    <td>杭州</td>
                    <td>杭州奥体中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2024-04-13 至 2024-04-14</td>
                    <td>重庆</td>
                    <td>重庆奥林匹克体育中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2024-04-20 至 2024-04-21</td>
                    <td>洛阳</td>
                    <td>洛阳市奥林匹克中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2024-04-26 至 2024-04-27</td>
                    <td>镇江</td>
                    <td>镇江体育会展中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2024-05-04 至 2024-05-05</td>
                    <td>太原</td>
                    <td>山西体育中心红灯笼体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2024-05-10 至 2024-05-12</td>
                    <td>厦门</td>
                    <td>厦门奥林匹克体育中心白鹭体育场</td>
                    <td>3场</td>
                  </tr>
                  <tr>
                    <td>2024-05-18 至 2024-05-19</td>
                    <td>乌鲁木齐</td>
                    <td>乌鲁木齐奥林匹克体育中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2024-05-25 至 2024-05-26</td>
                    <td>大连</td>
                    <td>大连体育中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2024-06-01</td>
                    <td>西宁</td>
                    <td>青海体育中心体育场</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2024-06-14 至 2024-06-16</td>
                    <td>长沙</td>
                    <td>长沙贺龙体育中心</td>
                    <td>3场</td>
                  </tr>
                  <tr>
                    <td>2024-06-21 至 2024-06-23</td>
                    <td>徐州</td>
                    <td>徐州奥林匹克体育中心体育场</td>
                    <td>3场</td>
                  </tr>
                  <tr>
                    <td>2024-06-29 至 2024-06-30</td>
                    <td>汕头</td>
                    <td>汕头体育中心体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2024-07-05 至 2024-07-06</td>
                    <td>呼和浩特</td>
                    <td>呼和浩特市体育场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2024-07-12 至 2024-07-20</td>
                    <td>北京</td>
                    <td>国家体育场"鸟巢"</td>
                    <td>6场</td>
                  </tr>
                  <tr>
                    <td>2024-07-25 至 2024-08-04</td>
                    <td>深圳</td>
                    <td>深圳大运中心体育场</td>
                    <td>6场</td>
                  </tr>
                  <tr>
                    <td>2024-08-09 至 2024-08-18</td>
                    <td>苏州</td>
                    <td>苏州奥林匹克体育中心体育场</td>
                    <td>6场</td>
                  </tr>
                  <tr>
                    <td>2025-02-14</td>
                    <td>新加坡</td>
                    <td>新加坡国家体育场</td>
                    <td>1场</td>
                  </tr>
                </tbody>
              </table>
            </td>
          </tr>
          <tr class="concert-row">
            <td>2018 至 2019</td>
            <td>薛之谦"摩天大楼"世界巡回演唱会</td>
            <td>{{ calculateTotal('摩天大楼') }}场</td>
            <td><button class="expand-btn" @click="toggleExpand('摩天大楼')"><img :src="'/src/assets/展开.svg'" :class="{ 'rotate-180': expandedConcert === '摩天大楼' }" alt="展开/收起" class="expand-icon"></button></td>
          </tr>
          <tr class="detail-row" v-show="expandedConcert === '摩天大楼'">
            <td colspan="4">
              <table class="detail-table">
                <thead>
                  <tr>
                    <th>分场举办时间</th>
                    <th>地点</th>
                    <th>场馆名称</th>
                    <th>场次</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>2018-07-14</td>
                    <td>北京</td>
                    <td>工人体育场</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2018-07-28</td>
                    <td>深圳</td>
                    <td>深圳湾体育中心体育场</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2018-08-11</td>
                    <td>成都</td>
                    <td>双流体育中心体育场</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2018-08-26</td>
                    <td>佛山</td>
                    <td>佛山世纪莲体育中心体育场</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2018-09-08</td>
                    <td>杭州</td>
                    <td>黄龙体育中心体育场</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2018-09-21</td>
                    <td>长沙</td>
                    <td>贺龙体育场</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2018-09-30</td>
                    <td>澳门</td>
                    <td>金光综艺馆</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2018-10-06,07</td>
                    <td>上海</td>
                    <td>虹口足球场</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2018-11-03</td>
                    <td>温哥华</td>
                    <td>UBC Thunderbird Sports Centre</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2018-11-06</td>
                    <td>多伦多</td>
                    <td>Paramount Fine Foods Centre</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2018-11-09</td>
                    <td>圣荷西</td>
                    <td>San Jose Centre</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2018-11-11</td>
                    <td>洛杉矶</td>
                    <td>Dolby Theatre</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2018-11-17</td>
                    <td>新加坡</td>
                    <td>新加坡室内体育馆</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2019-02-14</td>
                    <td>槟城</td>
                    <td>SPICE ARENA</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2019-02-16</td>
                    <td>吉隆坡</td>
                    <td>Axiata室内体育馆</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2019-03-10</td>
                    <td>伦敦</td>
                    <td>Wembley</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2019-04-01</td>
                    <td>墨尔本</td>
                    <td>墨尔本会展中心</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2019-04-04</td>
                    <td>奥克兰</td>
                    <td>The Trusts Arena</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2019-04-07</td>
                    <td>悉尼</td>
                    <td>Qudos Bank Arena</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2019-04-13/14</td>
                    <td>台北</td>
                    <td>台北小巨蛋</td>
                    <td>2场</td>
                  </tr>
                  <tr>
                    <td>2019-05-18</td>
                    <td>香港</td>
                    <td>亚洲国际博览馆</td>
                    <td>1场</td>
                  </tr>
                </tbody>
              </table>
            </td>
          </tr>
          <tr class="concert-row">
            <td>2017</td>
            <td>"我好像在哪见过你"全国巡回演唱会</td>
            <td>{{ calculateTotal('我好像在哪见过你') }}场</td>
            <td><button class="expand-btn" @click="toggleExpand('我好像在哪见过你')"><img :src="'/src/assets/展开.svg'" :class="{ 'rotate-180': expandedConcert === '我好像在哪见过你' }" alt="展开/收起" class="expand-icon"></button></td>
          </tr>
          <tr class="detail-row" v-show="expandedConcert === '我好像在哪见过你'">
            <td colspan="4">
              <table class="detail-table">
                <thead>
                  <tr>
                    <th>分场举办时间</th>
                    <th>地点</th>
                    <th>场馆名称</th>
                    <th>场次</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>2017-04-22</td>
                    <td>大连</td>
                    <td>大麦体育中心</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2017-04-29</td>
                    <td>深圳</td>
                    <td>深圳湾春蚕体育馆</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2017-05-20</td>
                    <td>青岛</td>
                    <td>国信体育馆</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2017-06-03</td>
                    <td>广州</td>
                    <td>国际体育演艺中心</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2017-06-10</td>
                    <td>上海</td>
                    <td>梅赛德斯奔驰文化中心</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2017-06-17</td>
                    <td>北京</td>
                    <td>五棵松体育馆</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2017-07-01</td>
                    <td>南京</td>
                    <td>南京奥体中心体育馆</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2017-07-08</td>
                    <td>成都</td>
                    <td>四川省体育馆</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2017-07-15</td>
                    <td>武汉</td>
                    <td>武汉体育中心体育馆</td>
                    <td>1场</td>
                  </tr>
                  <tr>
                    <td>2017-07-22</td>
                    <td>杭州</td>
                    <td>黄龙体育中心体育馆</td>
                    <td>1场</td>
                  </tr>
                </tbody>
              </table>
            </td>
          </tr>
          <tr class="concert-row">
            <td>2008-12-20</td>
            <td>"谦年传说"演唱会</td>
            <td>1场</td>
            <td><button class="expand-btn" @click="toggleExpand('谦年传说')"><img :src="'/src/assets/展开.svg'" :class="{ 'rotate-180': expandedConcert === '谦年传说' }" alt="展开/收起" class="expand-icon"></button></td>
          </tr>
          <tr class="detail-row" v-show="expandedConcert === '谦年传说'">
            <td colspan="4">
              <table class="detail-table">
                <thead>
                  <tr>
                    <th>分场举办时间</th>
                    <th>地点</th>
                    <th>场馆名称</th>
                    <th>场次</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>2008-12-20</td>
                    <td>上海</td>
                    <td>上海大舞台</td>
                    <td>1场</td>
                  </tr>
                </tbody>
              </table>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>

<!-- 代表作品 -->
<div class="works-section">
  <div class="container">
    <h2 class="section-title">
      <BoxReveal :duration="0.6" :delay="0.08" :blur="'10px'" :yOffset="20">
        <span>代表作品</span>
      </BoxReveal>
    </h2>
    <div class="works-grid">
      <FlipCard v-for="work in representativeWorks" :key="work.id" class="work-flip-card">
        <template #default>
          <div class="work-front" :style="{backgroundImage: `url(${work.image})`}">
            <div class="work-overlay"></div>
            <div class="work-info">
              <h3>{{ work.title }}</h3>
              <p>{{ work.year }}</p>
            </div>
          </div>
        </template>
        <template #back>
          <div class="work-back">
            <h4>{{ work.title }}</h4>
            <div class="work-details">
              <p><strong>发行年份:</strong> {{ work.year }}</p>
              <p><strong>类型:</strong> {{ work.type }}</p>
              <p><strong>成就:</strong></p>
              <ul>
                <li v-for="achievement in work.achievements" :key="achievement">{{ achievement }}</li>
              </ul>
              <p class="work-description">{{ work.description }}</p>
            </div>
          </div>
        </template>
      </FlipCard>
    </div>
  </div>
</div>
    

    <!-- 音乐成就 -->
    <div class="achievements-section">
      <div class="container">
        <h2 class="section-title">
          <BoxReveal :duration="0.6" :delay="0.08" :blur="'10px'" :yOffset="20">
            <span>音乐成就</span>
          </BoxReveal>
        </h2>
        <div class="achievements-list">
          <div class="achievement-item">
            <img class="achievement-icon" src="/src/assets/星球.png" alt="音乐成就图标">
            <h3><LetterPullup :words="'2006雪碧中国原创音乐流行榜'" :delay="0.05" /></h3>
            <p><LetterPullup :words="'最受欢迎新人奖'" :delay="0.04" /></p>
          </div>
          <div class="achievement-item">
            <img class="achievement-icon" src="/src/assets/星球.png" alt="音乐成就图标">
            <h3><LetterPullup :words="'2007东方风云榜'" :delay="0.05" /></h3>
            <p><LetterPullup :words="'最佳新人奖'" :delay="0.04" /></p>
          </div>
          <div class="achievement-item">
            <img class="achievement-icon" src="/src/assets/星球.png" alt="音乐成就图标">
            <h3><LetterPullup :words="'2014东方风云榜'" :delay="0.05" /></h3>
            <p><LetterPullup :words="'最佳唱作人奖'" :delay="0.04" /></p>
          </div>
          <div class="achievement-item">
            <img class="achievement-icon" src="/src/assets/星球.png" alt="音乐成就图标">
            <h3><LetterPullup :words="'2016全球华语歌曲排行榜'" :delay="0.05" /></h3>
            <p><LetterPullup :words="'最受欢迎男歌手、最受欢迎创作歌手奖'" :delay="0.04" /></p>
          </div>
          <div class="achievement-item">
            <img class="achievement-icon" src="/src/assets/星球.png" alt="音乐成就图标">
            <h3><LetterPullup :words="'2017全球流行音乐盛典'" :delay="0.05" /></h3>
            <p><LetterPullup :words="'年度最佳男歌手'" :delay="0.04" /></p>
          </div>
          <div class="achievement-item">
            <img class="achievement-icon" src="/src/assets/星球.png" alt="音乐成就图标">
            <h3><LetterPullup :words="'2017MusicRadio中国TOP排行榜'" :delay="0.05" /></h3>
            <p><LetterPullup :words="'内地最佳男歌手'" :delay="0.04" /></p>
          </div>
          <div class="achievement-item">
            <img class="achievement-icon" src="/src/assets/星球.png" alt="音乐成就图标">
            <h3><LetterPullup :words="'2019华人歌曲音乐盛典'" :delay="0.05" /></h3>
            <p><LetterPullup :words="'内地年度最受欢迎歌手奖、年度最佳专辑《怪咖》'" :delay="0.04" /></p>
          </div>
        </div>
      </div>
    </div>

    <div class="acting-section">
      <div class="container">
        <h2 class="section-title">
          <BoxReveal :duration="0.6" :delay="0.08" :blur="'10px'" :yOffset="20">
            <span>演艺经历</span>
          </BoxReveal>
        </h2>
        <div class="acting-content">
          <p><LetterPullup :words="'除了音乐事业，薛之谦还积极参与影视表演和综艺节目，展现了多方面的才华：'" :delay="0.03" /></p>

          <BoxReveal :duration="0.6" :delay="0.08" :blur="'10px'" :yOffset="20">
            <h3>电视剧作品</h3>
          </BoxReveal>
          <ul class="acting-list">
            <li><LetterPullup :words="'2015年 《妈妈像花儿一样》饰演 张幸福的弟弟'" :delay="0.04" /></li>
            <li><LetterPullup :words="'2017年 《我们的少年时代》饰演 陶西'" :delay="0.04" /></li>
          </ul>

          <BoxReveal :duration="0.6" :delay="0.08" :blur="'10px'" :yOffset="20">
            <h3>电影作品</h3>
          </BoxReveal>
          <ul class="acting-list">
            <li><LetterPullup :words="'2017年 《有完没完》饰演 和平'" :delay="0.04" /></li>
          </ul>

          <BoxReveal :duration="0.6" :delay="0.08" :blur="'10px'" :yOffset="20">
            <h3>综艺节目</h3>
          </BoxReveal>
          <ul class="acting-list">
            <li><LetterPullup :words="'2016-2018年 《火星情报局》高级特工'" :delay="0.04" /></li>
            <li><LetterPullup :words="'2017年 《明日之子》盛世独秀赛道星推官'" :delay="0.04" /></li>
            <li><LetterPullup :words="'2018年 《创造101》特邀嘉宾'" :delay="0.04" /></li>
          </ul>
        </div>
      </div>
    </div>

    <div class="music-style-section">
      <div class="container">
        <h2 class="section-title">
          <BoxReveal :duration="0.6" :delay="0.08" :blur="'10px'" :yOffset="20">
            <span>音乐风格</span>
          </BoxReveal>
        </h2>
        <div class="music-style-content">
          <p><LetterPullup :words="'薛之谦的音乐风格多样，融合了流行、摇滚、民谣、电子等多种元素。他的作品以情感真挚、歌词细腻著称，擅长用音乐表达生活中的感悟和情感故事。'" :delay="0.03" /></p>

          <div class="style-evolution">
            <BoxReveal :duration="0.6" :delay="0.08" :blur="'10px'" :yOffset="20">
              <h3>音乐风格演变</h3>
            </BoxReveal>
            <div class="evolution-item">
              <BoxReveal :duration="0.6" :delay="0.08" :blur="'10px'" :yOffset="20">
                <h4>早期（2005-2010年）</h4>
              </BoxReveal>
              <p><LetterPullup :words="'以抒情慢歌为主，如《认真的雪》《深深爱过你》等，旋律优美，歌词深情，展现了薛之谦出色的演唱实力和创作才华。'" :delay="0.03" /></p>
            </div>
            <div class="evolution-item">
              <BoxReveal :duration="0.6" :delay="0.08" :blur="'10px'" :yOffset="20">
                <h4>中期（2012-2015年）</h4>
              </BoxReveal>
              <p><LetterPullup :words="'开始尝试更多风格，如摇滚、民谣等，作品更加成熟，歌词也更加深刻，如《丑八怪》《绅士》等。'" :delay="0.03" /></p>
            </div>
            <div class="evolution-item">
              <BoxReveal :duration="0.6" :delay="0.08" :blur="'10px'" :yOffset="20">
                <h4>近期（2016-2023年）</h4>
              </BoxReveal>
              <p><LetterPullup :words="'音乐风格更加多元化，融合了电子、R&B、古典等元素，同时保持了情感真挚的特点，如《初学者》《渡 The Crossing》《怪咖》《天外来物》《无数》等专辑。'" :delay="0.03" /></p>
            </div>
          </div>

          <div class="style-characteristics">
            <BoxReveal :duration="0.6" :delay="0.08" :blur="'10px'" :yOffset="20">
              <h3>音乐特点</h3>
            </BoxReveal>
            <ul>
              <li><LetterPullup :words="'情感真挚：薛之谦的歌曲往往来源于真实的生活经历和情感体验，能够引发听众的共鸣。'" :delay="0.04" /></li>
              <li><LetterPullup :words="'歌词细腻：他的歌词注重细节描写，语言生动，能够准确表达复杂的情感。'" :delay="0.04" /></li>
              <li><LetterPullup :words="'旋律优美：他的歌曲旋律流畅，易于传唱，同时又不失艺术性。'" :delay="0.04" /></li>
              <li><LetterPullup :words="'风格多样：从抒情慢歌到摇滚、电子，薛之谦不断尝试不同的音乐风格，展现了多方面的才华。'" :delay="0.04" /></li>
              <li><LetterPullup :words="'独特嗓音：薛之谦的嗓音深情而富有辨识度，能够将歌曲中的情感完美传递给听众。'" :delay="0.04" /></li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <div class="charity-section">
      <div class="container">
        <h2 class="section-title">
          <BoxReveal :duration="0.6" :delay="0.08" :blur="'10px'" :yOffset="20">
            <span>社会贡献</span>
          </BoxReveal>
        </h2>
        <div class="charity-content">
          <p><LetterPullup :words="'薛之谦积极参与公益事业，实际行动回馈社会，传递正能量。'" :delay="0.03" /></p>

          <div class="charity-activities">
            <BoxReveal :duration="0.6" :delay="0.08" :blur="'10px'" :yOffset="20">
              <h3>公益活动</h3>
            </BoxReveal>
            <div class="charity-item">
              <BoxReveal :duration="0.6" :delay="0.08" :blur="'10px'" :yOffset="20">
                <h4>地震灾区捐款捐物</h4>
              </BoxReveal>
              <p><LetterPullup :words="'2008年汶川地震、2013年雅安地震等，薛之谦均第一时间捐款捐物，帮助灾区人民度过难关。'" :delay="0.04" /></p>
            </div>
            <div class="charity-item">
              <BoxReveal :duration="0.6" :delay="0.08" :blur="'10px'" :yOffset="20">
                <h4>慈善演唱会</h4>
              </BoxReveal>
              <p><LetterPullup :words="'多次参与慈善演唱会，为公益事业筹集善款。'" :delay="0.04" /></p>
            </div>
            <div class="charity-item">
              <BoxReveal :duration="0.6" :delay="0.08" :blur="'10px'" :yOffset="20">
                <h4>儿童教育支持</h4>
              </BoxReveal>
              <p><LetterPullup :words="'关注儿童教育，为贫困山区学校捐赠图书和教学设备。'" :delay="0.04" /></p>
            </div>
          </div>

          <div class="charity-quote">
            <blockquote>
              <p><LetterPullup :words="'「作为公众人物，我有责任用自己的影响力去帮助更多需要帮助的人，传递正能量。」'" :delay="0.03" /></p>
              <footer>— 薛之谦</footer>
            </blockquote>
          </div>
        </div>
      </div>
    </div>

    <!-- 图片墙 -->
    <div class="gallery-section">
      <div class="container">
        <h2 class="section-title">
          <BoxReveal :duration="0.6" :delay="0.08" :blur="'10px'" :yOffset="20">
            <span>精彩瞬间</span>
          </BoxReveal>
        </h2>
        <div class="gallery-grid">
          <div class="gallery-item" :style="{backgroundImage: 'url(https://picsum.photos/seed/album1/400/300)'}"></div>
          <div class="gallery-item" :style="{backgroundImage: 'url(https://picsum.photos/seed/album2/400/300)'}"></div>
          <div class="gallery-item" :style="{backgroundImage: 'url(https://picsum.photos/seed/album3/400/300)'}"></div>
          <div class="gallery-item" :style="{backgroundImage: 'url(https://picsum.photos/seed/album4/400/300)'}"></div>
          <div class="gallery-item" :style="{backgroundImage: 'url(https://picsum.photos/seed/album5/400/300)'}"></div>
          <div class="gallery-item" :style="{backgroundImage: 'url(https://picsum.photos/seed/album6/400/300)'}"></div>
        </div>
      </div>
    </div>
  </template>

  <script>
import LetterPullup from '@/components/LetterPullup.vue'
import FlipCard from '@/components/FlipCard.vue'
import BoxReveal from '@/components/BoxReveal.vue'
import LampEffect from '@/components/LampEffect.vue'
import Marquee from '@/components/Marquee.vue'

export default {
  components: {
    LetterPullup,
    FlipCard,
    BoxReveal,
    LampEffect,
    Marquee
  },
  data() {
    return {
      // 背景相关
      heroBackgroundStyle: {
        background: "url('@/assets/薛之谦.jpg') center/cover",
        filter: 'brightness(0.4)',
        transform: 'scale(1.02)'
      },
      
      // 跟踪当前展开的演唱会
      expandedConcert: null,
      showTable: true,
      // 演唱会数据
      concerts: [
        {
          name: '天外来物',
          time: '2021-10-23 至 2025-03-22',
          detail: [
            {time: '2021-10-23 至 2021-10-24', location: '苏州', venue: '苏州体育中心体育场',场次: 2},
            {time: '2021-11-27 至 2021-11-28', location: '广州', venue: '广州大学城体育中心体育场',场次: 2},
            {time: '2022-12-24', location: '海口', venue: '海口五源河体育场',场次: 1},
            {time: '2023-03-18 至 2023-03-19', location: '衢州', venue: '衢州体育中心体育场',场次: 2},
            {time: '2023-04-01', location: '南宁', venue: '南宁体育中心体育场',场次: 1},
            {time: '2023-04-14 至 2023-04-15', location: '佛山', venue: '佛山世纪莲体育中心体育场',场次: 2},
            {time: '2023-04-21 至 2023-04-22', location: '南昌', venue: '南昌国际体育中心体育场',场次: 2},
            {time: '2023-04-28 至 2023-04-29', location: '南京', venue: '南京奥林匹克体育中心体育场',场次: 2},
            {time: '2023-05-13 至 2023-05-14', location: '济南', venue: '济南奥林匹克体育中心体育场',场次: 2},
            {time: '2023-05-20 至 2023-05-21', location: '郑州', venue: '郑州奥林匹克体育中心体育场',场次: 2},
            {time: '2023-05-27', location: '连云港', venue: '连云港体育中心体育场',场次: 1},
            {time: '2023-06-03 至 2023-06-04', location: '武汉', venue: '武汉体育中心体育场',场次: 2},
            {time: '2023-06-10', location: '哈尔滨', venue: '哈尔滨国际会展中心体育场',场次: 1},
            {time: '2023-06-21 至 2023-06-23', location: '上海', venue: '上海虹口足球场',场次: 3},
            {time: '2023-07-01 至 2023-07-02', location: '西安', venue: '西安奥林匹克体育中心体育场',场次: 2},
            {time: '2023-07-08 至 2023-07-09', location: '青岛', venue: '青岛市民健身中心体育场',场次: 2},
            {time: '2023-07-14 至 2023-07-16', location: '衡阳', venue: '衡阳市体育中心体育场',场次: 3},
            {time: '2023-07-21 至 2023-07-23', location: '合肥', venue: '合肥体育中心体育场',场次: 3},
            {time: '2023-07-28 至 2023-07-29', location: '深圳', venue: '深圳大运中心体育场',场次: 2},
            {time: '2023-08-05 至 2023-08-06', location: '宜昌', venue: '宜昌奥林匹克体育中心体育场',场次: 2},
            {time: '2023-08-11 至 2023-08-13', location: '北京', venue: '国家体育场"鸟巢"',场次: 3},
            {time: '2023-08-18 至 2023-08-19', location: '兰州', venue: '兰州奥林匹克体育中心体育场',场次: 2},
            {time: '2023-08-26 至 2023-08-27', location: '遵义', venue: '遵义市奥林匹克体育中心体育场',场次: 2},
            {time: '2023-09-02 至 2023-09-03', location: '沈阳', venue: '沈阳奥林匹克体育中心体育场',场次: 2},
            {time: '2023-09-09', location: '成都', venue: '成都东安湖体育公园主体育场',场次: 1},
            {time: '2023-09-16 至 2023-09-17', location: '福州', venue: '福州海峡奥林匹克体育中心体育场',场次: 2},
            {time: '2023-09-22 至 2023-09-24', location: '广州', venue: '广州天河体育场',场次: 3},
            {time: '2023-09-29 至 2023-09-30', location: '滁州', venue: '滁州奥林匹克体育中心体育场',场次: 2},
            {time: '2023-10-05 至 2023-10-06', location: '石家庄', venue: '河北奥林匹克体育中心体育场',场次: 2},
            {time: '2023-10-14 至 2023-10-15', location: '天津', venue: '天津奥林匹克中心体育场',场次: 2},
            {time: '2023-10-20 至 2023-10-21', location: '常州', venue: '常州奥林匹克体育中心体育场',场次: 2},
            {time: '2023-10-27 至 2023-10-28', location: '清远', venue: '清远体育中心体育场',场次: 2},
            {time: '2023-11-03 至 2023-11-04', location: '成都', venue: '成都东安湖体育公园主体育场',场次: 2},
            {time: '2023-11-11 至 2023-11-12', location: '温州', venue: '温州奥林匹克体育中心主体育场',场次: 2},
            {time: '2023-11-19', location: '伦敦', venue: '伦敦温布利体育馆（OvO Arena Wembley）',场次: 1},
            {time: '2023-11-22', location: '巴黎', venue: '巴黎天顶体育馆（Zenith Paris - La Vilette）',场次: 1},
            {time: '2023-12-02 至 2023-12-03', location: '香港', venue: '香港亚洲国际博览馆',场次: 2},
            {time: '2023-12-08 至 2023-12-10', location: '澳门', venue: '澳门银河综艺馆',场次: 3},
            {time: '2023-12-16 至 2023-12-17', location: '三明', venue: '三明体育中心体育场',场次: 2},
            {time: '2023-12-23 至 2023-12-25', location: '吉隆坡', venue: '吉隆坡亚通体育馆（Axiata Arena）',场次: 3},
            {time: '2024-01-05 至 2024-01-07', location: '新加坡', venue: '新加坡室内体育馆',场次: 3},
            {time: '2024-01-13 至 2024-01-14', location: '曲靖', venue: '曲靖市文化体育公园体育场',场次: 2},
            {time: '2024-01-27', location: '海口', venue: '海口五源河体育场',场次: 1},
            {time: '2024-02-17', location: '纽约', venue: '纽约巴克莱中心（Barclay Center）',场次: 1},
            {time: '2024-02-24', location: '拉斯维加斯', venue: '拉斯维加斯米高梅花园剧场（MGM Grand Garden Arena）',场次: 1},
            {time: '2024-03-04', location: '圣荷西', venue: '圣荷西SAP中心（SAP Center）',场次: 1},
            {time: '2024-03-07', location: '温哥华', venue: '温哥华罗渣士体育馆（Rogers Arena）',场次: 1},
            {time: '2024-03-13', location: '多伦多', venue: '多伦多可口可乐竞技场（Coca-Cola Coliseum）',场次: 1},
            {time: '2024-03-23', location: '悉尼', venue: '悉尼库多斯银行体育馆（Qudos Bank Arena）',场次: 1},
            {time: '2024-03-26', location: '墨尔本', venue: '墨尔本罗德·拉沃尔球场（Rod Laver Arena）',场次: 1},
            {time: '2024-03-30', location: '奥克兰', venue: '奥克兰火花竞技场（Spark Arena）',场次: 1},
            {time: '2024-04-06 至 2024-04-07', location: '杭州', venue: '杭州奥体中心体育场',场次: 2},
            {time: '2024-04-13 至 2024-04-14', location: '重庆', venue: '重庆奥林匹克体育中心体育场',场次: 2},
            {time: '2024-04-20 至 2024-04-21', location: '洛阳', venue: '洛阳市奥林匹克中心体育场',场次: 2},
            {time: '2024-04-26 至 2024-04-27', location: '镇江', venue: '镇江体育会展中心体育场',场次: 2},
            {time: '2024-05-04 至 2024-05-05', location: '太原', venue: '山西体育中心红灯笼体育场',场次: 2},
            {time: '2024-05-10 至 2024-05-12', location: '厦门', venue: '厦门奥林匹克体育中心白鹭体育场',场次: 3},
            {time: '2024-05-18 至 2024-05-19', location: '乌鲁木齐', venue: '乌鲁木齐奥林匹克体育中心体育场',场次: 2},
            {time: '2024-05-25 至 2024-05-26', location: '大连', venue: '大连体育中心体育场',场次: 2},
            {time: '2024-06-01', location: '西宁', venue: '青海体育中心体育场',场次: 1},
            {time: '2024-06-14 至 2024-06-16', location: '长沙', venue: '长沙贺龙体育中心',场次: 3},
            {time: '2024-06-21 至 2024-06-23', location: '徐州', venue: '徐州奥林匹克体育中心体育场',场次: 3},
            {time: '2024-06-29 至 2024-06-30', location: '汕头', venue: '汕头体育中心体育场',场次: 2},
            {time: '2024-07-05 至 2024-07-06', location: '呼和浩特', venue: '呼和浩特市体育场',场次: 2},
            {time: '2024-07-12 至 2024-07-20', location: '北京', venue: '国家体育场"鸟巢"',场次: 6},
            {time: '2024-07-25 至 2024-08-04', location: '深圳', venue: '深圳大运中心体育场',场次: 6},
            {time: '2024-08-09 至 2024-08-18', location: '苏州', venue: '苏州奥林匹克体育中心体育场',场次: 6},
            {time: '2024-11-23', location: '吉隆坡', venue: '吉隆坡亚通体育馆（Axiata Arena）',场次: 1},
            {time: '2024-12-07', location: '曼谷', venue: '曼谷拉加曼加拉国家体育场',场次: 1},
            {time: '2025-01-25', location: '迪拜', venue: '迪拜 Coca-Cola Arena',场次: 1},
            {time: '2025-02-14', location: '新加坡', venue: '新加坡国家体育场',场次: 1},
            {time: '2025-03-08', location: '悉尼', venue: '悉尼库多斯银行体育馆（Qudos Bank Arena）',场次: 1},
            {time: '2025-03-15', location: '墨尔本', venue: '墨尔本罗德·拉沃尔球场（Rod Laver Arena）',场次: 1},
            {time: '2025-03-22', location: '奥克兰', venue: '奥克兰火花竞技场（Spark Arena）',场次: 1}
          ]
        },
        {          name: '摩天大楼',          time: '2018 至 2019',          detail: [            {time: '2018-07-14', location: '北京', venue: '工人体育场',场次: 1},            {time: '2018-07-28', location: '深圳', venue: '深圳湾体育中心体育场',场次: 1},            {time: '2018-08-11', location: '成都', venue: '双流体育中心体育场',场次: 1},            {time: '2018-08-26', location: '佛山', venue: '佛山世纪莲体育中心体育场',场次: 1},            {time: '2018-09-08', location: '杭州', venue: '黄龙体育中心体育场',场次: 1},            {time: '2018-09-21', location: '长沙', venue: '贺龙体育场',场次: 1},            {time: '2018-09-30', location: '澳门', venue: '金光综艺馆',场次: 1},            {time: '2018-10-06,07', location: '上海', venue: '虹口足球场',场次: 2},            {time: '2018-11-03', location: '温哥华', venue: 'UBC Thunderbird Sports Centre',场次: 1},            {time: '2018-11-06', location: '多伦多', venue: 'Paramount Fine Foods Centre',场次: 1},            {time: '2018-11-09', location: '圣荷西', venue: 'San Jose Centre',场次: 1},            {time: '2018-11-11', location: '洛杉矶', venue: 'Dolby Theatre',场次: 1},            {time: '2018-11-17', location: '新加坡', venue: '新加坡室内体育馆',场次: 1},            {time: '2019-02-14', location: '槟城', venue: 'SPICE ARENA',场次: 1},            {time: '2019-02-16', location: '吉隆坡', venue: 'Axiata室内体育馆',场次: 1},            {time: '2019-03-10', location: '伦敦', venue: 'Wembley',场次: 1},            {time: '2019-04-01', location: '墨尔本', venue: '墨尔本会展中心',场次: 1},            {time: '2019-04-04', location: '奥克兰', venue: 'The Trusts Arena',场次: 1},            {time: '2019-04-07', location: '悉尼', venue: 'Qudos Bank Arena',场次: 1},            {time: '2019-04-13/14', location: '台北', venue: '台北小巨蛋',场次: 2},            {time: '2019-05-18', location: '香港', venue: '亚洲国际博览馆',场次: 1},            {time: '2019-06-01', location: '重庆', venue: '重庆奥体中心体育场',场次: 1},            {time: '2019-06-08', location: '南京', venue: '南京奥体中心体育场',场次: 1}          ]        },
        {
          name: '我好像在哪见过你',
          time: '2017',
          detail: [
            {time: '2017-04-22', location: '大连', venue: '大麦体育中心',场次: 1},
            {time: '2017-04-29', location: '深圳', venue: '深圳湾春蚕体育馆',场次: 1},
            {time: '2017-05-20', location: '青岛', venue: '国信体育馆',场次: 1},
            {time: '2017-06-03', location: '广州', venue: '国际体育演艺中心',场次: 1},
            {time: '2017-06-10', location: '上海', venue: '梅赛德斯奔驰文化中心',场次: 1},
            {time: '2017-06-17', location: '北京', venue: '五棵松体育馆',场次: 1},
            {time: '2017-07-01', location: '南京', venue: '南京奥体中心体育馆',场次: 1},
            {time: '2017-07-08', location: '成都', venue: '四川省体育馆',场次: 1},
            {time: '2017-07-15', location: '武汉', venue: '武汉体育中心体育馆',场次: 1},
            {time: '2017-07-22', location: '杭州', venue: '黄龙体育中心体育馆',场次: 1}
          ]
        },
        {
          name: '谦年传说',
          time: '2008-12-20',
          detail: [
            {time: '2008-12-20', location: '上海', venue: '上海大舞台',场次: 1}
          ]
        }
      ],
      // 代表作品数据
      representativeWorks: [
        {
          id: 1,
          title: '认真的雪',
          year: '2006年',
          type: '单曲',
          image: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=300&h=300&fit=crop&q=80',
          achievements: ['雪碧中国原创音乐流行榜最受欢迎新人奖', '东方风云榜最佳新人奖'],
          description: '薛之谦的成名作，奠定了其在华语乐坛的地位。深情的演唱和真挚的情感表达深受听众喜爱。'
        },
        {
          id: 2,
          title: '演员',
          year: '2015年',
          type: '单曲',
          image: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=300&h=300&fit=crop&q=80',
          achievements: ['全球华语歌曲排行榜年度金曲', 'MV播放量破亿'],
          description: '薛之谦复出后的代表作之一，以其独特的创作风格和深刻的歌词内容获得广泛认可。'
        },
        {
          id: 3,
          title: '丑八怪',
          year: '2013年',
          type: '单曲',
          image: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=300&h=300&fit=crop&q=80',
          achievements: ['各大音乐平台热门单曲', '演唱会必唱曲目'],
          description: '一首充满自嘲和幽默的作品，展现了薛之谦独特的音乐个性和创作才华。'
        },
        {
          id: 4,
          title: '绅士',
          year: '2015年',
          type: '单曲',
          image: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=300&h=300&fit=crop&q=80',
          achievements: ['华语流行音乐榜前十', '各大音乐节点播率极高'],
          description: '展现了薛之谦在音乐创作上的成熟和多样性，歌曲旋律朗朗上口。'
        },
        {
          id: 5,
          title: '刚刚好',
          year: '2016年',
          type: '单曲',
          image: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=300&h=300&fit=crop&q=80',
          achievements: ['年度热门金曲', '各大音乐平台冠军单曲'],
          description: '温暖治愈的情歌，体现了薛之谦细腻的情感表达能力。'
        },
        {
          id: 6,
          title: '天外来物',
          year: '2020年',
          type: '专辑',
          image: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=300&h=300&fit=crop&q=80',
          achievements: ['年度最佳专辑提名', '全国巡回演唱会主题'],
          description: '薛之谦近年来的重要作品，展现了其音乐创作的新高度和艺术追求。'
        }
      ]
    };
  },
  methods: {
    // 切换展开/折叠状态
    toggleExpand(itemName) {
      if (this.expandedConcert === itemName) {
        this.expandedConcert = null;
      } else {
        this.expandedConcert = itemName;
      }
      // CONSOLE LOG REMOVED: console.log('Expanded concert:', this.expandedConcert);
    },
    // 计算演唱会总场次
    calculateTotal(concertName) {
      const concert = this.concerts.find(c => c.name === concertName);
      if (!concert) return 0;
      return concert.detail.reduce((total, item) => total + item.场次, 0);
    }
  },
  mounted() {
    // 为body添加CSS类，以应用0717页面特定的样式
    document.body.classList.add('page-0717');
  },
  beforeUnmount() {
    // 页面销毁时移除CSS类
    document.body.classList.remove('page-0717');
  }
};
</script>

<style scoped>
/* 全局样式 */
.xue-theme-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #2c3e50 0%, #4ca1af 100%);
  color: var(--text-primary);
  font-family: 'Arial', sans-serif;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

/* 英雄区域 */
.hero-section {
  height: 100vh;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

/* 夸赞语句样式 */
.praise-messages {
  display: flex;
  gap: 3rem;
  align-items: center;
  padding: 1rem 0;
}

.praise-item {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  padding: 1rem 2rem;
  border-radius: 30px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: white;
  font-size: 1.1rem;
  white-space: nowrap;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.praise-item:hover {
  transform: translateY(-3px);
  background: rgba(255, 255, 255, 0.15);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.3);
}

.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('@/assets/薛之谦.jpg') center/cover;
  filter: brightness(0.4);
  transform: scale(1.02);
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to bottom, rgba(0,0,0,0.3) 0%, rgba(0,0,0,0.7) 100%);
}

.hero-content {
  text-align: center;
  z-index: 10;
  position: relative;
  padding: 2rem;
  opacity: 1;
  transform: translateY(0);
}

.hero-avatar-container {
  width: 180px;
  height: 180px;
  margin: 0 auto 1.5rem;
  border-radius: 50%;
  border: 5px solid rgba(255, 255, 255, 0.2);
  overflow: hidden;
  box-shadow: 0 0 30px rgba(0, 0, 0, 0.3);
  transition: transform 0.3s ease;
  position: relative;
}

.hero-avatar-container:hover {
  transform: scale(1.05);
  box-shadow: 0 0 40px rgba(100, 149, 237, 0.6);
}

.hero-avatar {
  width: 100%;
  height: 100%;
  background-image: url('@/assets/跃.jpg');
  background-size: cover;
  background-position: center;
  position: relative;
  z-index: 1;
}

.avatar-link {
  display: inline-block;
  cursor: pointer;
}

.hero-title {
  font-size: 4.5rem;
  font-weight: bold;
  margin-bottom: 1rem;
  text-shadow: 0 4px 8px rgba(0,0,0,0.5);
  letter-spacing: 2px;
}

.hero-subtitle {
  font-size: 1.6rem;
  margin-bottom: 2rem;
  opacity: 0.9;
  font-weight: 300;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background: rgba(255,255,255,0.2);
  padding: 0.75rem 1.5rem;
  border-radius: 50px;
  margin-bottom: 2rem;
  backdrop-filter: blur(5px);
  transition: all 0.3s ease;
}

.hero-badge:hover {
  background: rgba(255,255,255,0.3);
  transform: translateY(-3px);
}

.celebration-icon {
  font-size: 1.2rem;
}

.hero-stats {
  display: flex;
  gap: 3rem;
  margin: 0;
  padding: 0 1rem;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
  color: #ffffff;
  text-shadow: 0 2px 8px rgba(0,0,0,0.3);
}

.stat-label {
  font-size: 1rem;
  color: #ffffff;
  opacity: 0.9;
  text-shadow: 0 1px 4px rgba(0,0,0,0.2);
}

/* 章节标题 */
.section-title {
  text-align: center;
  margin-bottom: 3rem;
  position: relative;
  font-size: 2rem;
}

.section-title span {
  padding: 0 1.5rem;
  position: relative;
  z-index: 2;
  color: var(--text-primary);
  font-weight: bold;
}

/* 非黑色主题下增强文本对比度 */
:not([data-theme="black"]) .profile-section, 
:not([data-theme="black"]) .personal-info-section, 
:not([data-theme="black"]) .career-section {
  --text-enhanced: var(--text-primary);
}

:not([data-theme="black"]) .profile-card p,
:not([data-theme="black"]) .timeline-content p {
  color: var(--text-enhanced);
  font-weight: 500;
}

:not([data-theme="black"]) .info-value {
  color: var(--text-enhanced);
}

.section-title::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  width: 100%;
  height: 1px;
  background: rgba(255,255,255,0.2);
  z-index: 1;
}

/* 个人档案 */
.profile-section {
  padding: 6rem 2rem;
  background: var(--background);
}

.profile-card {
  background: rgba(255,255,255,0.05);
  padding: 2rem;
  border-radius: 15px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255,255,255,0.1);
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
}

.profile-content-wrapper {
  text-align: left;
  line-height: 1.6;
}

.profile-animated-text {
  color: var(--text-primary) !important;
  font-size: inherit !important;
  font-weight: inherit !important;
  line-height: inherit !important;
}

/* 移除了章节标题动画样式，因为个人档案不再使用动画效果 */

/* 移除了 .profile-description 样式，因为现在所有内容都使用动画效果 */

.personal-info-section {
  padding: 6rem 2rem;
  background: var(--background);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-top: 2rem;
}

.info-item {
  background: rgba(255,255,255,0.05);
  padding: 1.5rem;
  border-radius: 10px;
  backdrop-filter: blur(5px);
  transition: all 0.3s ease;
  border: 1px solid rgba(255,255,255,0.05);
}

.info-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.1);
  background: rgba(255,255,255,0.08);
}

.info-label {
  display: block;
  font-size: 1rem;
  opacity: 0.9;
  margin-bottom: 0.5rem;
  color: var(--text-secondary);
}

.info-value {
  font-size: 1.2rem;
  font-weight: 500;
}

/* 音乐生涯时间轴 */
.career-section {
  padding: 6rem 2rem;
  background: var(--background);
}

.timeline {
  position: relative;
  max-width: 800px;
  margin: 0 auto;
}

.timeline::after {
  content: '';
  position: absolute;
  width: 3px;
  background: linear-gradient(to bottom, #4ca1af 0%, #2c3e50 100%);
  top: 0;
  bottom: 0;
  left: 50%;
  margin-left: -1.5px;
  border-radius: 3px;
}

.timeline-item {
  padding: 20px 40px;
  position: relative;
  width: 50%;
  box-sizing: border-box;
  opacity: 1;
  transform: translateY(0);
}

.timeline-item:nth-child(odd) {
  left: 0;
}

.timeline-item:nth-child(even) {
  left: 50%;
}

.timeline-date {
  font-size: 1.3rem;
  font-weight: bold;
  margin-bottom: 1rem;
  color: var(--text-primary);
  display: inline-block;
  padding: 0.3rem 1rem;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 20px;
}

.timeline-content {
  padding: 1.8rem;
  background: rgba(255,255,255,0.1);
  border-radius: 15px;
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255,255,255,0.15);
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.timeline-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255,255,255,0.1) 0%, rgba(255,255,255,0.05) 100%);
  border-radius: 15px;
  z-index: -1;
}

.timeline-content:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 40px rgba(0,0,0,0.15);
  background: rgba(255,255,255,0.15);
  border-color: rgba(255,255,255,0.25);
}

.timeline-content:hover::before {
  background: linear-gradient(135deg, rgba(255,255,255,0.15) 0%, rgba(255,255,255,0.1) 100%);
}


.timeline-content h3 {
  margin-top: 0;
  font-size: 1.5rem;
  margin-bottom: 0.8rem;
  color: var(--text-primary);
  position: relative;
  z-index: 1;
}

.timeline-content p {
  opacity: 0.9;
  line-height: 1.6;
  position: relative;
  z-index: 1;
  margin: 0;
  font-size: 0.95rem;
  color: var(--text-secondary);
  transition: color 0.3s ease;
}

.timeline-content:hover p {
  color: var(--text-primary);
  opacity: 1;
}

/* 时间线子项目样式 */
.timeline-subitem {
  margin: 1rem 0;
  padding-left: 1rem;
  border-left: 2px solid rgba(255, 255, 255, 0.2);
}

.timeline-subdate {
  font-size: 1rem;
  font-weight: bold;
  color: var(--text-primary);
  margin-bottom: 0.5rem;
  display: inline-block;
  padding: 0.2rem 0.8rem;
  background: rgba(76, 161, 175, 0.2);
  border-radius: 12px;
  border: 1px solid rgba(76, 161, 175, 0.3);
}

/* 黑色主题下的时间线子项目样式 */
[data-theme="black"] .timeline-subitem {
  border-left: 2px solid rgba(255, 255, 255, 0.3);
}

[data-theme="black"] .timeline-subdate {
  color: #ffffff;
  background: rgba(76, 161, 175, 0.3);
  border: 1px solid rgba(76, 161, 175, 0.5);
}

/* 代表作品 */
.works-section {
  padding: 6rem 2rem;
  background: rgba(0,0,0,0.1);
}

.works-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 2rem;
  margin-top: 2rem;
}

.work-item {
  height: 350px;
  border-radius: 10px;
  overflow: hidden;
  position: relative;
  background-size: cover;
  background-position: center;
  transition: all 0.3s ease;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

.work-item:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 30px rgba(0,0,0,0.2);
}

.work-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to top, rgba(0,0,0,0.8) 0%, rgba(0,0,0,0) 60%);
}

.work-item h3 {
  position: absolute;
  bottom: 2rem;
  left: 1.5rem;
  font-size: 1.5rem;
  margin: 0;
  color: var(--text-primary);
  z-index: 10;
}

.work-item p {
  position: absolute;
  bottom: 1rem;
  left: 1.5rem;
  font-size: 0.9rem;
  margin: 0;
  color: var(--text-secondary);
  z-index: 10;
}

.work-desc {
  position: absolute;
  bottom: 3.5rem;
  left: 1.5rem;
  font-size: 0.9rem;
  margin: 0;
  color: var(--text-secondary);
  z-index: 10;
  max-width: 85%;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 音乐成就 */
.achievements-section {
  padding: 6rem 2rem;
  background: rgba(255,255,255,0.03);
}

.achievements-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 2rem;
  margin-top: 2rem;
}

.achievement-item {
  background: var(--card-bg);
  padding: 2rem;
  border-radius: 10px;
  backdrop-filter: blur(5px);
  border: 1px solid var(--card-border);
  transition: all 0.3s ease;
  text-align: center;
  box-shadow: var(--card-shadow);
}

.achievement-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0,0,0,0.15);
  background: var(--card-hover-bg);
}

.achievement-video {
  width: 100%;
  max-width: 200px;
  height: auto;
  margin: 0 auto 1.5rem;
  border-radius: 10px;
  display: block;
}

.achievement-icon {
  width: 100%;
  max-width: 200px;
  height: auto;
  margin: 0 auto 1.5rem;
  border-radius: 10px;
  display: block;
}

.achievement-item h3 {
  margin-top: 0;
  font-size: 1.3rem;
  margin-bottom: 0.8rem;
  color: var(--text-primary);
}

.achievement-item p {
  color: var(--text-secondary);
  opacity: 0.9;
  margin: 0;
}

/* 演艺经历 */
.acting-section {
  padding: 6rem 2rem;
  background: rgba(0,0,0,0.1);
}

.acting-content {
  max-width: 800px;
  margin: 0 auto;
  color: var(--text-primary) !important;
}

.acting-content p {
  color: var(--text-secondary) !important;
}

.acting-content h3 {
  margin-top: 2rem;
  margin-bottom: 1rem;
  font-size: 1.5rem;
  color: var(--text-primary);
}

.acting-list {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.acting-list li {
  padding: 1rem 0;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  position: relative;
  padding-left: 6rem;
}

.acting-list li:last-child {
  border-bottom: none;
}

.year {
  position: absolute;
  left: 0;
  top: 1rem;
  font-weight: bold;
  color: var(--text-primary);
  width: 5rem;
  display: inline-block;
}

/* 全局卡片变量定义 */
:root {
  --card-bg: rgba(255,255,255,0.05);
  --card-border: rgba(255,255,255,0.05);
  --card-shadow: 0 5px 15px rgba(0,0,0,0.05);
}

/* 白色主题卡片变量覆盖 */
[data-theme="white"] {
  --card-bg: rgba(255,255,255,1);
  --card-border: rgba(209,213,219,1);
  --card-shadow: 0 10px 30px rgba(0,0,0,0.1);
  --card-hover-bg: rgba(255,255,255,0.95);
}

/* 音乐风格 */
.music-style-section {
  padding: 6rem 2rem;
  background: rgba(255,255,255,0.03);
}

.music-style-content {
  max-width: 800px;
  margin: 0 auto;
  color: var(--text-primary) !important;
}

.music-style-content p {
  color: var(--text-secondary) !important;
}

.style-evolution {
  margin-top: 2rem;
}

.evolution-item {
  margin-bottom: 2rem;
  padding: 1.5rem;
  background: var(--card-bg);
  border-radius: 10px;
  border: 1px solid var(--card-border);
  box-shadow: var(--card-shadow);
}

.evolution-item h4 {
  margin-top: 0;
  color: var(--text-primary);
}

.style-characteristics {
  margin-top: 2rem;
}

.style-characteristics ul {
  list-style-type: none;
  padding: 0;
}

.style-characteristics li {
  padding: 1rem 0;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.style-characteristics li:last-child {
  border-bottom: none;
}

.characteristic {
  font-weight: bold;
  color: var(--text-primary);
  margin-right: 0.5rem;
}

/* 社会贡献 */
.charity-section {
  padding: 6rem 2rem;
  background: rgba(0,0,0,0.1);
}

.charity-content {
  max-width: 800px;
  margin: 0 auto;
  color: var(--text-primary) !important;
}

.charity-content p {
  color: var(--text-secondary) !important;
}

.charity-content li {
  color: var(--text-secondary) !important;
}

.charity-activities {
  margin-top: 2rem;
}

.charity-item {
  margin-bottom: 2rem;
  padding: 1.5rem;
  background: var(--card-bg);
  border-radius: 10px;
  border: 1px solid var(--card-border);
  box-shadow: var(--card-shadow);
}

.charity-item h4 {
  margin-top: 0;
  color: var(--text-primary);
}

.charity-quote {
  margin-top: 3rem;
  padding: 2rem;
  background: rgba(255,255,255,0.05);
  border-radius: 10px;
  text-align: center;
}

blockquote {
  margin: 0;
  font-style: italic;
  font-size: 1.2rem;
}

blockquote footer {
  margin-top: 1rem;
  font-style: normal;
  font-size: 1rem;
  color: var(--text-primary);
}

/* 图片墙 */
.gallery-section {
  padding: 6rem 2rem;
  background: rgba(255,255,255,0.03);
}

.gallery-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 1.5rem;
  margin-top: 2rem;
}

.gallery-item {
  height: 300px;
  border-radius: 10px;
  overflow: hidden;
  background-size: cover;
  background-position: center;
  transition: all 0.3s ease;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

.gallery-item:hover {
  transform: scale(1.03);
  box-shadow: 0 15px 30px rgba(0,0,0,0.2);
}

/* 动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 演唱会记录样式 */
.concert-info-card {
  background: var(--card-bg);
  border-radius: 10px;
  padding: 2rem;
  margin: 2rem 0;
  backdrop-filter: blur(5px);
  border: 1px solid var(--card-border);
  box-shadow: var(--card-shadow);
  transition: all 0.3s ease;
}

.concert-info-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0,0,0,0.15);
  background: var(--card-hover-bg);
}

.info-header {
  border-bottom: 1px solid rgba(255,255,255,0.1);
  padding-bottom: 1rem;
  margin-bottom: 1.5rem;
}

.info-header h3 {
  margin: 0;
  font-size: 1.5rem;
  color: var(--text-primary);
}

.info-content {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1rem;
}

.info-content p {
  margin: 0.5rem 0;
  line-height: 1.6;
}

.info-content strong {
  color: #fff;
  margin-right: 0.5rem;
}

.concert-table-container {
  width: 100%;
  overflow-x: auto;
  margin: 2rem 0;
}

.concert-table {
  width: 100%;
  border-collapse: collapse;
}

.concert-table th {
  background-color: rgba(255, 255, 255, 0.1);
  color: var(--text-primary);
  text-align: left;
  padding: 1rem;
}

.concert-row {
  background-color: rgba(255, 255, 255, 0.05);
  transition: background-color 0.3s;
  color: var(--text-primary);
}

.concert-row:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.concert-table td {
  padding: 1rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  color: var(--text-primary);
}

/* 黑色主题下的演唱会表格样式 */
[data-theme="black"] .concert-table th {
  background-color: rgba(255, 255, 255, 0.05);
  color: #ffffff;
}

[data-theme="black"] .concert-row {
  background-color: rgba(255, 255, 255, 0.02);
  color: #ffffff;
}

[data-theme="black"] .concert-row:hover {
  background-color: rgba(255, 255, 255, 0.05);
}

[data-theme="black"] .concert-table td {
  color: #ffffff;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.expand-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 5px;
  transition: transform 0.3s;
}

.expand-icon {
  width: 20px;
  height: 20px;
  transition: transform 0.3s ease;
}

.rotate-180 {
  transform: rotate(180deg);
}

.expand-btn:hover {
  transform: scale(1.2);
}

.detail-row {
  background-color: var(--background-secondary, rgba(0, 0, 0, 0.1));
  display: table-row;
  height: auto;
  min-height: 100px;
  overflow: visible;
}

.detail-row td {
  border: none !important;
  padding: 0 !important;
}

.detail-table {
  width: 100%;
  border-collapse: collapse;
  border: none;
}

.detail-table th {
  background-color: var(--card-bg, rgba(255, 255, 255, 0.1));
  color: var(--text-primary);
  padding: 0.5rem;
  border: none;
}

.detail-table td {
  padding: 0.5rem;
  border: none;
  border-bottom: 1px solid var(--card-border, rgba(255, 255, 255, 0.05));
  color: var(--text-primary);
  background-color: transparent;
}

.detail-table tr:nth-child(even) td {
  background-color: rgba(255, 255, 255, 0.03);
}

.detail-table tr:hover td {
  background-color: rgba(255, 255, 255, 0.08);
  transition: background-color 0.3s ease;
}

/* 主题特定样式 */
[data-theme="black"] .detail-row {
  background-color: rgba(0, 0, 0, 0.5);
}

[data-theme="black"] .detail-row td {
  border: none !important;
  padding: 0 !important;
}

[data-theme="black"] .detail-table {
  background-color: rgba(0, 0, 0, 0.3);
}

[data-theme="black"] .detail-table th {
  background-color: rgba(0, 0, 0, 0.7);
  color: #ffffff;
  border: none;
}

[data-theme="black"] .detail-table td {
  color: #ffffff;
  background-color: rgba(0, 0, 0, 0.4);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

[data-theme="black"] .detail-table tr:nth-child(even) td {
  background-color: rgba(0, 0, 0, 0.6);
}

[data-theme="black"] .detail-table tr:hover td {
  background-color: rgba(255, 255, 255, 0.08);
}

/* 粉色主题 */
[data-theme="pink"] .detail-row {
  background-color: rgba(255, 182, 193, 0.3);
}

[data-theme="pink"] .detail-row td {
  border: none !important;
  padding: 0 !important;
}

[data-theme="pink"] .detail-table {
  background-color: rgba(255, 182, 193, 0.2);
}

[data-theme="pink"] .detail-table th {
  background-color: rgba(255, 182, 193, 0.7);
  color: #ffffff;
  border: none;
}

[data-theme="pink"] .detail-table td {
  color: var(--text-primary);
  background-color: rgba(255, 182, 193, 0.3);
  border-bottom: 1px solid rgba(255, 182, 193, 0.3);
}

[data-theme="pink"] .detail-table tr:nth-child(even) td {
  background-color: rgba(255, 182, 193, 0.4);
}

[data-theme="pink"] .detail-table tr:hover td {
  background-color: rgba(255, 182, 193, 0.5);
}

/* 蓝色主题 */
[data-theme="blue"] .detail-row {
  background-color: rgba(135, 206, 235, 0.3);
}

[data-theme="blue"] .detail-row td {
  border: none !important;
  padding: 0 !important;
}

[data-theme="blue"] .detail-table {
  background-color: rgba(135, 206, 235, 0.2);
}

[data-theme="blue"] .detail-table th {
  background-color: rgba(135, 206, 235, 0.7);
  color: #ffffff;
  border: none;
}

[data-theme="blue"] .detail-table td {
  color: var(--text-primary);
  background-color: rgba(135, 206, 235, 0.3);
  border-bottom: 1px solid rgba(135, 206, 235, 0.3);
}

[data-theme="blue"] .detail-table tr:nth-child(even) td {
  background-color: rgba(135, 206, 235, 0.4);
}

[data-theme="blue"] .detail-table tr:hover td {
  background-color: rgba(135, 206, 235, 0.5);
}

/* 绿色主题 */
[data-theme="green"] .detail-row {
  background-color: rgba(144, 238, 144, 0.3);
}

[data-theme="green"] .detail-row td {
  border: none !important;
  padding: 0 !important;
}

[data-theme="green"] .detail-table {
  background-color: rgba(144, 238, 144, 0.2);
}

[data-theme="green"] .detail-table th {
  background-color: rgba(144, 238, 144, 0.7);
  color: #ffffff;
  border: none;
}

[data-theme="green"] .detail-table td {
  color: var(--text-primary);
  background-color: rgba(144, 238, 144, 0.3);
  border-bottom: 1px solid rgba(144, 238, 144, 0.3);
}

[data-theme="green"] .detail-table tr:nth-child(even) td {
  background-color: rgba(144, 238, 144, 0.4);
}

[data-theme="green"] .detail-table tr:hover td {
  background-color: rgba(144, 238, 144, 0.5);
}

/* 紫色主题 */
[data-theme="purple"] .detail-row {
  background-color: rgba(221, 160, 221, 0.3);
}

[data-theme="purple"] .detail-row td {
  border: none !important;
  padding: 0 !important;
}

[data-theme="purple"] .detail-table {
  background-color: rgba(221, 160, 221, 0.2);
}

[data-theme="purple"] .detail-table th {
  background-color: rgba(221, 160, 221, 0.7);
  color: #ffffff;
  border: none;
}

[data-theme="purple"] .detail-table td {
  color: var(--text-primary);
  background-color: rgba(221, 160, 221, 0.3);
  border-bottom: 1px solid rgba(221, 160, 221, 0.3);
}

[data-theme="purple"] .detail-table tr:nth-child(even) td {
  background-color: rgba(221, 160, 221, 0.4);
}

[data-theme="purple"] .detail-table tr:hover td {
  background-color: rgba(221, 160, 221, 0.5);
}

/* 橙色主题 */
[data-theme="orange"] .detail-row {
  background-color: rgba(255, 165, 0, 0.3);
}

[data-theme="orange"] .detail-row td {
  border: none !important;
  padding: 0 !important;
}

[data-theme="orange"] .detail-table {
  background-color: rgba(255, 165, 0, 0.2);
}

[data-theme="orange"] .detail-table th {
  background-color: rgba(255, 165, 0, 0.7);
  color: #ffffff;
  border: none;
}

[data-theme="orange"] .detail-table td {
  color: var(--text-primary);
  background-color: rgba(255, 165, 0, 0.3);
  border-bottom: 1px solid rgba(255, 165, 0, 0.3);
}

[data-theme="orange"] .detail-table tr:nth-child(even) td {
  background-color: rgba(255, 165, 0, 0.4);
}

[data-theme="orange"] .detail-table tr:hover td {
  background-color: rgba(255, 165, 0, 0.5);
}

/* 红色主题 */
[data-theme="red"] .detail-row {
  background-color: rgba(255, 99, 99, 0.3);
}

[data-theme="red"] .detail-row td {
  border: none !important;
  padding: 0 !important;
}

[data-theme="red"] .detail-table {
  background-color: rgba(255, 99, 99, 0.2);
}

[data-theme="red"] .detail-table th {
  background-color: rgba(255, 99, 99, 0.7);
  color: #ffffff;
  border: none;
}

[data-theme="red"] .detail-table td {
  color: var(--text-primary);
  background-color: rgba(255, 99, 99, 0.3);
  border-bottom: 1px solid rgba(255, 99, 99, 0.3);
}

[data-theme="red"] .detail-table tr:nth-child(even) td {
  background-color: rgba(255, 99, 99, 0.4);
}

[data-theme="red"] .detail-table tr:hover td {
  background-color: rgba(255, 99, 99, 0.5);
}

/* 黄色主题 */
[data-theme="yellow"] .detail-row {
  background-color: rgba(255, 223, 0, 0.3);
}

[data-theme="yellow"] .detail-row td {
  border: none !important;
  padding: 0 !important;
}

[data-theme="yellow"] .detail-table {
  background-color: rgba(255, 223, 0, 0.2);
}

[data-theme="yellow"] .detail-table th {
  background-color: rgba(255, 223, 0, 0.7);
  color: #000000;
  border: none;
}

[data-theme="yellow"] .detail-table td {
  color: var(--text-primary);
  background-color: rgba(255, 223, 0, 0.3);
  border-bottom: 1px solid rgba(255, 223, 0, 0.3);
}

[data-theme="yellow"] .detail-table tr:nth-child(even) td {
  background-color: rgba(255, 223, 0, 0.4);
}

[data-theme="yellow"] .detail-table tr:hover td {
  background-color: rgba(255, 223, 0, 0.5);
}

/* 白色主题 */
[data-theme="white"] .detail-row {
  background-color: rgba(0, 0, 0, 0.05);
}

[data-theme="white"] .detail-row td {
  border: none !important;
  padding: 0 !important;
}

[data-theme="white"] .detail-table {
  background-color: rgba(255, 255, 255, 0.9);
}

[data-theme="white"] .detail-table th {
  background-color: rgba(0, 0, 0, 0.1);
  color: #ffffff;
  border: none;
}

[data-theme="white"] .detail-table td {
  color: #333333;
  background-color: rgba(255, 255, 255, 0.8);
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
}

[data-theme="white"] .detail-table tr:nth-child(even) td {
  background-color: rgba(0, 0, 0, 0.05);
}

[data-theme="white"] .detail-table tr:hover td {
  background-color: rgba(0, 0, 0, 0.1);
}

/* 代表作品样式 */
.works-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  justify-content: center;
  gap: 1.5rem;
  margin-top: var(--space-lg);
  padding: 2rem;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}

/* 响应式布局 */
@media (max-width: 1024px) {
  .works-grid {
    grid-template-columns: repeat(4, 1fr);
    gap: 1.2rem;
  }
}

@media (max-width: 768px) {
  .works-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 1rem;
  }
}

@media (max-width: 480px) {
  .works-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 0.8rem;
    padding: 1rem;
  }
}

/* FlipCard 前面样式 */
.work-front {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  position: relative;
  border-radius: 16px;
  overflow: hidden;
}

.work-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to top, rgba(0,0,0,0.8) 0%, rgba(0,0,0,0) 60%);
}

.work-info {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 1.5rem;
  z-index: 10;
}

.work-info h3 {
  font-size: 1.25rem;
  font-weight: 600;
  margin: 0 0 0.5rem 0;
  color: white;
}

.work-info p {
  font-size: 0.875rem;
  margin: 0;
  color: rgba(255, 255, 255, 0.8);
}

/* FlipCard 背面样式 */
.work-back {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  color: white;
}

.work-back h4 {
  font-size: 1rem;
  font-weight: 600;
  margin: 0 0 0.8rem 0;
  color: #fff;
  text-align: center;
}

.work-details {
  flex: 1;
  overflow: hidden;
}

.work-details p {
  font-size: 0.7rem;
  margin: 0 0 0.4rem 0;
  line-height: 1.2;
}

.work-details strong {
  color: #fbbf24;
}

.work-details ul {
  margin: 0.3rem 0;
  padding-left: 0.8rem;
  font-size: 0.65rem;
}

.work-details li {
  margin: 0.2rem 0;
  line-height: 1.2;
}

.work-description {
  font-size: 0.65rem !important;
  line-height: 1.3 !important;
  margin-top: 0.5rem !important;
  color: rgba(255, 255, 255, 0.9) !important;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.work-item-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}

.work-item {
  width: 100%;
  height: 200px;
  border-radius: 8px;
  overflow: hidden;
  background-size: cover;
  background-position: center;
  transition: transform var(--transition-normal);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.work-item:hover {
  transform: translateY(-5px);
}

.work-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8), transparent);
  opacity: 0.8;
}

.work-item-container h3 {
  color: var(--text-primary);
  margin-top: 1rem;
  margin-bottom: 0.5rem;
  font-size: 1.2rem;
  text-align: center;
}

.work-item-container p {
  color: var(--text-primary);
  margin: 0;
  font-size: 1rem;
  font-weight: 500;
  text-align: center;
  margin-top: 0.25rem;
}

.work-desc {
  display: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-title {
    font-size: 3rem;
  }

  .hero-subtitle {
    font-size: 1.2rem;
  }

  .hero-stats {
    flex-wrap: wrap;
    gap: 1.5rem;
  }

  .timeline::after {
    left: 31px;
  }

  .timeline-item {
    width: 100%;
    padding-left: 70px;
    padding-right: 25px;
  }

  .timeline-item:nth-child(even) {
    left: 0;
  }

  .info-grid,
  .achievements-list,
  .gallery-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .hero-title {
    font-size: 2.5rem;
  }

  .hero-avatar-container {
    width: 150px;
    height: 150px;
  }

  .section-title {
    font-size: 1.8rem;
  }

  .acting-list li {
    padding-left: 0;
  }

  .year {
    position: static;
    display: block;
    margin-bottom: 0.5rem;
  }
}
</style>
