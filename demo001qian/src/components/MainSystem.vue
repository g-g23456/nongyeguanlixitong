<template>
  <div class="main-system" :class="'role-' + userInfo.role">
    <div class="loading-overlay" :class="{ active: loadingVisible }">
      <div class="spinner"></div>
      <div class="loading-text">{{ loadingText }}</div>
      <div class="loading-detail">{{ loadingDetail }}</div>
    </div>

    <div class="layout">
      <div class="sidebar">
        <div class="logo">
          <h2>🌾 AI农业资源调度平台</h2>
          <p>十五五数字农业· 纯软件系统</p>
        </div>
        <div class="menu">
          <div
            class="menu-item"
            :class="{ active: currentPage === 'dashboard' }"
            data-page="dashboard"
            @click="switchPage('dashboard')"
          >
            <span class="icon">📊</span><span>数据驾驶舱</span>
          </div>

          <div class="menu-group">
            <div class="menu-item" @click="toggleSubmenu('farmland')">
              <span class="icon">🌱</span><span>耕地资源调配</span>
              <span style="margin-left: auto"></span>
            </div>
            <div class="submenu" :class="{ open: openMenus.farmland }">
              <div
                class="submenu-item"
                :class="{ active: currentPage === 'farmland-list' }"
                data-page="farmland-list"
                @click="switchPage('farmland-list')"
              >
                耕地地块台账
              </div>
              <div
                class="submenu-item"
                :class="{ active: currentPage === 'farmland-optimize' }"
                data-page="farmland-optimize"
                @click="switchPage('farmland-optimize')"
              >
                AI种植结构优化
              </div>
              <div
                class="submenu-item"
                :class="{ active: currentPage === 'farmland-rotation' }"
                data-page="farmland-rotation"
                @click="switchPage('farmland-rotation')"
              >
                轮作休耕规划
              </div>
            </div>
          </div>

          <div class="menu-group">
            <div class="menu-item" @click="toggleSubmenu('water')">
              <span class="icon">💧</span><span>水资源配额调度</span>
              <span style="margin-left: auto"></span>
            </div>
            <div class="submenu" :class="{ open: openMenus.water }">
              <div
                class="submenu-item"
                :class="{ active: currentPage === 'water-quota' }"
                data-page="water-quota"
                @click="switchPage('water-quota')"
              >
                用水配额数据管理
              </div>
              <div
                class="submenu-item"
                :class="{ active: currentPage === 'water-allocation' }"
                data-page="water-allocation"
                @click="switchPage('water-allocation')"
              >
                AI水量优化分配
              </div>
              <div
                class="submenu-item"
                :class="{ active: currentPage === 'water-analysis' }"
                data-page="water-analysis"
                @click="switchPage('water-analysis')"
              >
                用水数据分析评估
              </div>
            </div>
          </div>

          <div class="menu-group">
            <div class="menu-item" @click="toggleSubmenu('seed')">
              <span class="icon">🧪</span><span>农资资源调配</span>
              <span style="margin-left: auto"></span>
            </div>
            <div class="submenu" :class="{ open: openMenus.seed }">
              <div
                class="submenu-item"
                :class="{ active: currentPage === 'seed-inventory' }"
                data-page="seed-inventory"
                @click="switchPage('seed-inventory')"
              >
                农资出入库台账
              </div>
              <div
                class="submenu-item"
                :class="{ active: currentPage === 'seed-allocation' }"
                data-page="seed-allocation"
                @click="switchPage('seed-allocation')"
              >
                AI按需分配算法
              </div>
              <div
                class="submenu-item"
                :class="{ active: currentPage === 'seed-predict' }"
                data-page="seed-predict"
                @click="switchPage('seed-predict')"
              >
                消耗预测与库存预警
              </div>
            </div>
          </div>

          <div class="menu-group">
            <div class="menu-item" @click="toggleSubmenu('labor')">
              <span class="icon">👷</span><span>人力农事资源调配</span>
              <span style="margin-left: auto"></span>
            </div>
            <div class="submenu" :class="{ open: openMenus.labor }">
              <div
                class="submenu-item"
                :class="{ active: currentPage === 'labor-list' }"
                data-page="labor-list"
                @click="switchPage('labor-list')"
              >
                劳动力资源信息库
              </div>
              <div
                class="submenu-item"
                :class="{ active: currentPage === 'labor-schedule' }"
                data-page="labor-schedule"
                @click="switchPage('labor-schedule')"
              >
                智能排班调度
              </div>
            </div>
          </div>

          <div class="menu-group admin-only dispatcher-only">
            <div class="menu-item" @click="toggleSubmenu('equipment')">
              <span class="icon">🚜</span><span>器械资源调配</span>
              <span style="margin-left: auto"></span>
            </div>
            <div class="submenu" :class="{ open: openMenus.equipment }">
              <div
                class="submenu-item"
                :class="{ active: currentPage === 'equipment-list' }"
                data-page="equipment-list"
                @click="switchPage('equipment-list')"
              >
                农机具台账管理
              </div>
              <div
                class="submenu-item"
                :class="{ active: currentPage === 'equipment-allocation' }"
                data-page="equipment-allocation"
                @click="switchPage('equipment-allocation')"
              >
                智能调配与路径优化
              </div>
              <div
                class="submenu-item"
                :class="{ active: currentPage === 'equipment-maintenance' }"
                data-page="equipment-maintenance"
                @click="switchPage('equipment-maintenance')"
              >
                维护保养管理
              </div>
            </div>
          </div>

          <div
            class="menu-item"
            :class="{ active: currentPage === 'ai-decision' }"
            data-page="ai-decision"
            @click="switchPage('ai-decision')"
          >
            <span class="icon">🤖</span><span>AI综合决策中心</span>
          </div>

          <div class="menu-group admin-only analyst-only">
            <div class="menu-item" @click="toggleSubmenu('predict')">
              <span class="icon">📈</span><span>大数据分析预测</span>
              <span style="margin-left: auto"></span>
            </div>
            <div class="submenu" :class="{ open: openMenus.predict }">
              <div
                class="submenu-item"
                :class="{ active: currentPage === 'predict-yield' }"
                data-page="predict-yield"
                @click="switchPage('predict-yield')"
              >
                作物产量预测
              </div>
              <div
                class="submenu-item"
                :class="{ active: currentPage === 'predict-resource' }"
                data-page="predict-resource"
                @click="switchPage('predict-resource')"
              >
                资源需求智能预测
              </div>
            </div>
          </div>

          <div
            class="menu-item admin-only"
            :class="{ active: currentPage === 'sys-user' }"
            data-page="sys-user"
            @click="switchPage('sys-user')"
          >
            <span class="icon">⚙️</span><span>系统管理（RBAC）</span>
          </div>
        </div>
      </div>

      <div class="main">
        <div class="header">
          <div class="breadcrumb">
            <span>{{ breadcrumb }}</span> / <span>{{ subBreadcrumb }}</span>
          </div>
          <div class="header-right">
            <div class="user-info">
              <div class="avatar">{{ userInfo.name ? userInfo.name.charAt(0) : 'U' }}</div>
              <div>
                <div class="user-name">
                  {{ userInfo.name || '用户' }}
                  <span class="role-tag" :class="userInfo.role">{{
                    userInfo.roleName || '系统管理员'
                  }}</span>
                </div>
              </div>
            </div>
            <button class="logout-btn" @click="logout">退出登录</button>
          </div>
        </div>

        <div class="content">
          <div class="page" :class="{ active: currentPage === 'dashboard' }" id="page-dashboard">
            <div class="dash-grid">
              <div class="dash-stat">
                <div class="icon green">🌾</div>
                <div>
                  <h3>12,856 万亩</h3>
                  <p>耕地总面积</p>
                </div>
              </div>
              <div class="dash-stat">
                <div class="icon blue">💧</div>
                <div>
                  <h3>2,450 万m³</h3>
                  <p>年度用水配额</p>
                </div>
              </div>
              <div class="dash-stat">
                <div class="icon orange">🧪</div>
                <div>
                  <h3>8,650 万吨</h3>
                  <p>农资库存总量</p>
                </div>
              </div>
              <div class="dash-stat">
                <div class="icon purple">🚜</div>
                <div>
                  <h3>156 台</h3>
                  <p>农机具总数</p>
                </div>
              </div>
            </div>
            <div class="chart-row">
              <div class="chart-box">
                <div class="chart-title">📊 农业资源调配总览（五维数据）</div>
                <div ref="chartDash1" style="height: 300px"></div>
              </div>
              <div class="chart-box">
                <div class="chart-title">🗺 各片区资源分布热图</div>
                <div ref="chartDash2" style="height: 300px"></div>
              </div>
            </div>
            <div class="chart-row">
              <div class="chart-box">
                <div class="chart-title">📈 资源利用率AI评分趋势</div>
                <div ref="chartDash3" style="height: 260px"></div>
              </div>
              <div class="chart-box">
                <div class="chart-title">⚠️ 资源调配异常预警</div>
                <div style="padding: 12px">
                  <div
                    style="
                      display: flex;
                      align-items: center;
                      gap: 10px;
                      padding: 10px;
                      background: #fffbe6;
                      border-radius: 6px;
                      margin-bottom: 8px;
                    "
                  >
                    <span>⚠️</span>
                    <span style="font-size: 13px"
                      >西片区用水配额使用率已达
                      <strong style="color: #faad14">89.2%</strong>，建议启动节水方案</span
                    >
                  </div>
                  <div
                    style="
                      display: flex;
                      align-items: center;
                      gap: 10px;
                      padding: 10px;
                      background: #fff1f0;
                      border-radius: 6px;
                      margin-bottom: 8px;
                    "
                  >
                    <span>🔴</span>
                    <span style="font-size: 13px"
                      >南片区播种机C型
                      <strong style="color: #f5222d">逾期维护12天</strong>，请尽快安排检修</span
                    >
                  </div>
                  <div
                    style="
                      display: flex;
                      align-items: center;
                      gap: 10px;
                      padding: 10px;
                      background: #e6f7ff;
                      border-radius: 6px;
                      margin-bottom: 8px;
                    "
                  >
                    <span>ℹ️</span>
                    <span style="font-size: 13px"
                      >化肥库存低于安全阈值，AI建议
                      <strong style="color: #1890ff">补充600吨复合肥</strong></span
                    >
                  </div>
                  <div
                    style="
                      display: flex;
                      align-items: center;
                      gap: 10px;
                      padding: 10px;
                      background: #f6ffed;
                      border-radius: 6px;
                    "
                  >
                    <span>✅</span>
                    <span style="font-size: 13px"
                      >东片区AI种植结构优化方案已执行，<strong style="color: #52c41a"
                        >预计增产12.5%</strong
                      ></span
                    >
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div
            class="page"
            :class="{ active: currentPage === 'farmland-list' }"
            id="page-farmland-list"
          >
            <div class="card">
              <div class="card-header">
                <div class="card-title">🌱 耕地地块信息数字化台账</div>
                <div>
                  <button class="btn btn-primary" @click="showFarmlandCreateForm = true">
                    + 新增地块
                  </button>
                  <button class="btn btn-outline" style="margin-left: 8px">导出Excel</button>
                </div>
              </div>
              <div v-if="showFarmlandCreateForm" class="card" style="margin-bottom: 16px">
                <div class="card-header">
                  <div class="card-title">📝 新增地块</div>
                  <button class="btn btn-outline" @click="showFarmlandCreateForm = false">
                    �?
                  </button>
                </div>
                <div class="form-row">
                  <div class="form-group">
                    <label>地块编号</label>
                    <input v-model="farmlandCreateForm.blockCode" placeholder="�?F005" />
                  </div>
                  <div class="form-group">
                    <label>地块名称</label>
                    <input v-model="farmlandCreateForm.name" placeholder="�?东区B地块" />
                  </div>
                  <div class="form-group">
                    <label>所属片区</label>
                    <select v-model="farmlandCreateForm.regionId">
                      <option :value="1">东片区</option>
                      <option :value="2">西片区</option>
                      <option :value="3">南片区</option>
                      <option :value="4">北片区</option>
                    </select>
                  </div>
                </div>
                <div class="form-row">
                  <div class="form-group">
                    <label>面积(亩)</label>
                    <input
                      v-model.number="farmlandCreateForm.area"
                      type="number"
                      placeholder="请输入面积"
                    />
                  </div>
                  <div class="form-group">
                    <label>土壤类型</label>
                    <select v-model="farmlandCreateForm.soilType">
                      <option value="黑土">黑土</option>
                      <option value="黄土">黄土</option>
                      <option value="红土">红土</option>
                      <option value="沙壤土">沙壤土</option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label>当前作物</label>
                    <input v-model="farmlandCreateForm.currentCrop" placeholder="水稻" />
                  </div>
                </div>
                <div class="form-row">
                  <div class="form-group">
                    <label>权属</label>
                    <select v-model="farmlandCreateForm.ownership">
                      <option value="村集体">村集体</option>
                      <option value="农户承包">农户承包</option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label>可种植周期</label>
                    <input v-model="farmlandCreateForm.plantingPeriod" placeholder="4-10月" />
                  </div>
                  <div class="form-group">
                    <label>状态</label>
                    <select v-model="farmlandCreateForm.status">
                      <option value="种植中">种植中</option>
                      <option value="待播种">待播种</option>
                      <option value="休耕">休耕</option>
                    </select>
                  </div>
                </div>
                <div class="form-row">
                  <div class="form-group">
                    <label>适宜作物（逗号分隔）</label>
                    <input v-model="farmlandCreateForm.suitableCrops" placeholder="水稻,小麦" />
                  </div>
                </div>
                <div style="margin-top: 12px">
                  <button class="btn btn-primary" @click="submitFarmlandCreate">提交</button>
                  <button
                    class="btn btn-outline"
                    style="margin-left: 8px"
                    @click="showFarmlandCreateForm = false"
                  >
                    取消
                  </button>
                </div>
              </div>
              <div class="stats-row">
                <div class="stat-card">
                  <div class="stat-value">{{ farmlandStats.totalArea.toLocaleString() }}</div>
                  <div class="stat-label">总面积（亩）</div>
                </div>
                <div class="stat-card success">
                  <div class="stat-value">{{ farmlandStats.plantedArea.toLocaleString() }}</div>
                  <div class="stat-label">已种植（亩）</div>
                </div>
                <div class="stat-card warning">
                  <div class="stat-value">{{ farmlandStats.pendingArea.toLocaleString() }}</div>
                  <div class="stat-label">待播种（亩）</div>
                </div>
                <div class="stat-card info">
                  <div class="stat-value">{{ farmlandStats.fallowArea.toLocaleString() }}</div>
                  <div class="stat-label">休耕复用（亩）</div>
                </div>
              </div>
              <div v-if="farmlandLoading" style="text-align: center; padding: 40px">
                加载中.....
              </div>
              <table v-else class="data-table">
                <thead>
                  <tr>
                    <th>地块编号</th>
                    <th>所属片区</th>
                    <th>面积(亩)</th>
                    <th>土壤类型</th>
                    <th>适宜作物</th>
                    <th>当前作物</th>
                    <th>权属</th>
                    <th>可种植周期</th>
                    <th>状态</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in farmlandList" :key="item.blockCode">
                    <td>{{ item.blockCode }}</td>
                    <td>{{ item.region }}</td>
                    <td>{{ item.area.toLocaleString() }}</td>
                    <td>{{ item.soilType }}</td>
                    <td>{{ (item.suitableCrops || []).join('/') }}</td>
                    <td>{{ item.currentCrop }}</td>
                    <td>{{ item.ownership }}</td>
                    <td>{{ item.plantingPeriod }}</td>
                    <td>
                      <span
                        class="tag"
                        :class="{
                          'tag-success': item.status === '种植中',
                          'tag-warning': item.status === '待播种',
                          'tag-info': item.status === '休耕',
                        }"
                        >{{ item.status }}</span
                      >
                    </td>
                  </tr>
                  <tr v-if="farmlandList.length === 0">
                    <td colspan="9" style="text-align: center; padding: 20px">暂无数据</td>
                  </tr>
                </tbody>
              </table>
              <div class="pagination" v-if="farmlandTotal > 0">
                <span>{{ farmlandTotal }} </span>
                <button
                  class="btn btn-outline"
                  :disabled="farmlandPage <= 1"
                  @click="farmlandPage--; loadFarmlandList()"
                >
                  上一页
                </button>
                <span>{{ farmlandPage }} / {{ farmlandTotalPages }} </span>
                <button
                  class="btn btn-outline"
                  :disabled="farmlandPage >= farmlandTotalPages"
                  @click="farmlandPage++; loadFarmlandList()"
                >
                  下一页
                </button>
              </div>
            </div>
          </div>

          <div
            class="page"
            :class="{ active: currentPage === 'farmland-optimize' }"
            id="page-farmland-optimize"
          >
            <div class="card">
              <div class="card-header">
                <div class="card-title">🤖 AI耕地种植结构优化算法</div>
                <span class="tag tag-info">遗传算法 + 多约束优化</span>
              </div>
              <div class="form-row" style="margin-bottom: 16px">
                <div class="form-group">
                  <label>优化目标</label>
                  <select v-model="optimizeForm.goal">
                    <option value="maximizeYield">产量最大化</option>
                    <option value="maximizeProfit">利润最大化</option>
                    <option value="minimizeRisk">风险最小化</option>
                    <option value="balanced">资源均衡</option>
                  </select>
                </div>
                <div class="form-group">
                  <label>约束条件</label>
                  <select v-model="optimizeForm.constraintMode">
                    <option value="waterLimit">水土资源上限</option>
                    <option value="marketDemand">市场需求导向</option>
                    <option value="climateFit">气候数据适配</option>
                  </select>
                </div>
                <div class="form-group">
                  <label>数据年份</label>
                  <select v-model="optimizeForm.year">
                    <option :value="2021">2021</option>
                    <option :value="2022">2022</option>
                    <option :value="2023">2023</option>
                    <option :value="2024">2024</option>
                    <option :value="2025">2025</option>
                    <option :value="2026">2026</option>
                  </select>
                </div>
                <div class="form-group">
                  <button class="btn btn-primary" @click="runAIOptimize">🚀 启动AI优化计算</button>
                </div>
              </div>
              <div v-if="aiOptimizeResultVisible" id="aiOptimizeResult">
                <div class="ai-panel">
                  <h4>中AI优化计算完成（{{ optimizeForm.year }}年{{ optimizeGoalLabel }}优化中</h4>
                  <div class="progress-item">
                    <div class="progress-label">
                      <span>目标函数收敛图</span>
                      <span
                        >{{
                          ((optimizeMetrics.objectiveConvergence || 0.987) * 100).toFixed(1)
                        }}%</span
                      >
                    </div>
                    <div class="progress-bar">
                      <div
                        class="progress-fill"
                        :style="{
                          width: (optimizeMetrics.objectiveConvergence || 0.987) * 100 + '%',
                        }"
                      ></div>
                    </div>
                  </div>
                  <div class="progress-item">
                    <div class="progress-label">
                      <span>约束条件满足度</span>
                      <span
                        >{{
                          ((optimizeMetrics.constraintSatisfactionRate || 1) * 100).toFixed(1)
                        }}%</span
                      >
                    </div>
                    <div class="progress-bar">
                      <div
                        class="progress-fill"
                        :style="{
                          width: (optimizeMetrics.constraintSatisfactionRate || 1) * 100 + '%',
                        }"
                      ></div>
                    </div>
                  </div>
                  <div style="margin-top: 12px; font-size: 13px; color: #555">
                    📈 最优方案：
                    <span v-for="(item, index) in optimizeCropDistribution" :key="item.name">
                      {{ item.name }} {{ item.percentage.toFixed(1) }}%
                      <span v-if="index < optimizeCropDistribution.length - 1">| </span>
                    </span>
                  </div>
                </div>
              </div>
              <div class="chart-row">
                <div class="chart-box">
                  <div class="chart-title">🥧 优化后种植结构分布（玫瑰图）</div>
                  <div ref="chartCropRose" style="height: 280px"></div>
                </div>
                <div class="chart-box">
                  <div class="chart-title">📊 地块-作物适配度AI评分</div>
                  <div ref="chartCropScore" style="height: 280px"></div>
                </div>
              </div>
            </div>
          </div>

          <div
            class="page"
            :class="{ active: currentPage === 'farmland-rotation' }"
            id="page-farmland-rotation"
          >
            <div class="card">
              <div class="card-header">
                <div class="card-title">🌱 轮作休耕资源智能分配规划</div>
              </div>
              <div ref="chartRotation" style="height: 380px"></div>
            </div>
          </div>

          <div
            class="page"
            :class="{ active: currentPage === 'water-quota' }"
            id="page-water-quota"
          >
            <div class="card">
              <div class="card-header">
                <div class="card-title">💧 区域年度/季度用水配额数据管理</div>
                <div>
                  <button class="btn btn-primary">+ 配额调整</button>
                  <button class="btn btn-outline" style="margin-left: 8px">导出报表</button>
                </div>
              </div>
              <div class="stats-row">
                <div class="stat-card info">
                  <div class="stat-value">2,450</div>
                  <div class="stat-label">年度总配额（万m³）</div>
                </div>
                <div class="stat-card">
                  <div class="stat-value">1,870</div>
                  <div class="stat-label">已分配（万m³）</div>
                </div>
                <div class="stat-card success">
                  <div class="stat-value">580</div>
                  <div class="stat-label">剩余可调配（万m³）</div>
                </div>
                <div class="stat-card warning">
                  <div class="stat-value">76.3%</div>
                  <div class="stat-label">整体使用率</div>
                </div>
              </div>
              <table class="data-table">
                <thead>
                  <tr>
                    <th>片区</th>
                    <th>年度配额(万m³)</th>
                    <th>已使用</th>
                    <th>剩余</th>
                    <th>使用量</th>
                    <th>作物类型</th>
                    <th>AI评估</th>
                    <th>状态</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>东片区</td>
                    <td>800</td>
                    <td>620</td>
                    <td>180</td>
                    <td>77.5%</td>
                    <td>水稻为主</td>
                    <td><span class="tag tag-success">合理</span></td>
                    <td><span class="tag tag-success">正常</span></td>
                  </tr>
                  <tr>
                    <td>西片区</td>
                    <td>650</td>
                    <td>580</td>
                    <td>70</td>
                    <td>89.2%</td>
                    <td>小麦/玉米</td>
                    <td><span class="tag tag-warning">偏高</span></td>
                    <td><span class="tag tag-warning">注意</span></td>
                  </tr>
                  <tr>
                    <td>南片区</td>
                    <td>550</td>
                    <td>380</td>
                    <td>170</td>
                    <td>69.1%</td>
                    <td>玉米/大豆</td>
                    <td><span class="tag tag-success">合理</span></td>
                    <td><span class="tag tag-success">正常</span></td>
                  </tr>
                  <tr>
                    <td>北片区</td>
                    <td>450</td>
                    <td>290</td>
                    <td>160</td>
                    <td>64.4%</td>
                    <td>大豆/小麦</td>
                    <td><span class="tag tag-success">合理</span></td>
                    <td><span class="tag tag-success">正常</span></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <div
            class="page"
            :class="{ active: currentPage === 'water-allocation' }"
            id="page-water-allocation"
          >
            <div class="card">
              <div class="card-header">
                <div class="card-title">🤖 AI水量优化分配算法</div>
                <span class="tag tag-info">线性规划+ 约束优化</span>
              </div>
              <div class="form-row" style="margin-bottom: 16px">
                <div class="form-group">
                  <label>分配周期</label>
                  <select>
                    <option>季度分配</option>
                    <option>月度分配</option>
                  </select>
                </div>
                <div class="form-group">
                  <label>优化目标</label>
                  <select>
                    <option>节水最大化</option>
                    <option>产量最大化</option>
                    <option>均衡分配</option>
                  </select>
                </div>
                <div class="form-group">
                  <button class="btn btn-primary" @click="runWaterAI">🚀 执行AI水量分配</button>
                </div>
              </div>
              <div class="chart-row">
                <div class="chart-box">
                  <div class="chart-title">💧 传统方案 vs AI优化方案</div>
                  <div ref="chartWaterCompare" style="height: 280px"></div>
                </div>
                <div class="chart-box">
                  <div class="chart-title">📊 各片区用水效率雷达图</div>
                  <div ref="chartWaterRadar" style="height: 280px"></div>
                </div>
              </div>
            </div>
          </div>

          <div
            class="page"
            :class="{ active: currentPage === 'water-analysis' }"
            id="page-water-analysis"
          >
            <div class="card">
              <div class="card-header">
                <div class="card-title">💧 历史用水数据分析与浪费度智能评估</div>
              </div>
              <div class="chart-row">
                <div class="chart-box">
                  <div class="chart-title">📈 年度用水趋势与预测</div>
                  <div ref="chartWaterTrend" style="height: 260px"></div>
                </div>
                <div class="chart-box">
                  <div class="chart-title">⚠️ 用水浪费度评估分析</div>
                  <div ref="chartWaterWaste" style="height: 260px"></div>
                </div>
              </div>
              <div class="card">
                <div class="card-header"><div class="card-title">📋 用水浪费度AI评估报告</div></div>
                <table class="data-table">
                  <thead>
                    <tr>
                      <th>片区</th>
                      <th>实际用水</th>
                      <th>理论需求</th>
                      <th>浪费量</th>
                      <th>浪费率</th>
                      <th>AI建议</th>
                      <th>节水潜力</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td>东片区</td>
                      <td>620万m³</td>
                      <td>580万m³</td>
                      <td>40万m³</td>
                      <td>6.9%</td>
                      <td>优化灌溉时段</td>
                      <td>6.5%</td>
                    </tr>
                    <tr>
                      <td>西片区</td>
                      <td>580万m³</td>
                      <td>520万m³</td>
                      <td>60万m³</td>
                      <td>11.5%</td>
                      <td>升级滴灌系统</td>
                      <td>10.3%</td>
                    </tr>
                    <tr>
                      <td>南片区</td>
                      <td>380万m³</td>
                      <td>360万m³</td>
                      <td>20万m³</td>
                      <td>5.6%</td>
                      <td>维持现状</td>
                      <td>5.3%</td>
                    </tr>
                    <tr>
                      <td>北片区</td>
                      <td>290万m³</td>
                      <td>270万m³</td>
                      <td>20万m³</td>
                      <td>7.4%</td>
                      <td>喷灌改滴灌</td>
                      <td>6.9%</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          <div
            class="page"
            :class="{ active: currentPage === 'seed-inventory' }"
            id="page-seed-inventory"
          >
            <div class="card">
              <div class="card-header">
                <div class="card-title">🧪 农资出入库纯软件台账管理</div>
                <div>
                  <button class="btn btn-primary">+ 入库登记</button>
                  <button class="btn btn-outline" style="margin-left: 8px">调拨审批</button>
                </div>
              </div>
              <div class="stats-row">
                <div class="stat-card">
                  <div class="stat-value">2,180</div>
                  <div class="stat-label">复合肥（吨）</div>
                </div>
                <div class="stat-card">
                  <div class="stat-value">1,560</div>
                  <div class="stat-label">尿素（吨）</div>
                </div>
                <div class="stat-card warning">
                  <div class="stat-value">64.3</div>
                  <div class="stat-label">杀虫剂（吨）⚠️偏高</div>
                </div>
                <div class="stat-card">
                  <div class="stat-value">85</div>
                  <div class="stat-label">水稻种子（吨）</div>
                </div>
              </div>
              <table class="data-table">
                <thead>
                  <tr>
                    <th>物资编号</th>
                    <th>名称</th>
                    <th>类型</th>
                    <th>库存量</th>
                    <th>安全阈值</th>
                    <th>单位</th>
                    <th>有效期</th>
                    <th>AI预警</th>
                    <th>状态</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>S001</td>
                    <td>复合肥</td>
                    <td>化肥</td>
                    <td>2,180</td>
                    <td>1,500</td>
                    <td>�?</td>
                    <td>2025-06</td>
                    <td><span class="tag tag-success">充足</span></td>
                    <td><span class="tag tag-success">正常</span></td>
                  </tr>
                  <tr>
                    <td>S002</td>
                    <td>尿素</td>
                    <td>化肥</td>
                    <td>1,560</td>
                    <td>1,000</td>
                    <td>�?</td>
                    <td>2025-03</td>
                    <td><span class="tag tag-success">充足</span></td>
                    <td><span class="tag tag-success">正常</span></td>
                  </tr>
                  <tr>
                    <td>S003</td>
                    <td>杀虫剂</td>
                    <td>农药</td>
                    <td>64.3</td>
                    <td>80</td>
                    <td>�?</td>
                    <td>2024-12</td>
                    <td><span class="tag tag-warning">低于阈值</span></td>
                    <td><span class="tag tag-warning">补货</span></td>
                  </tr>
                  <tr>
                    <td>S004</td>
                    <td>水稻种子</td>
                    <td>种子</td>
                    <td>85</td>
                    <td>60</td>
                    <td>�?</td>
                    <td>2025-01</td>
                    <td><span class="tag tag-success">充足</span></td>
                    <td><span class="tag tag-success">正常</span></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <div
            class="page"
            :class="{ active: currentPage === 'seed-allocation' }"
            id="page-seed-allocation"
          >
            <div class="card">
              <div class="card-header">
                <div class="card-title">🤖 AI农资按需配比与智能分配算法</div>
                <span class="tag tag-info">多约束优化模型</span>
              </div>
              <div class="form-row" style="margin-bottom: 16px">
                <div class="form-group">
                  <label>目标作物</label>
                  <select>
                    <option>全部作物</option>
                    <option>水稻</option>
                    <option>小麦</option>
                    <option>玉米</option>
                  </select>
                </div>
                <div class="form-group">
                  <label>分配策略</label>
                  <select>
                    <option>按种植面积比例</option>
                    <option>按作物需肥量</option>
                    <option>成本最小化</option>
                  </select>
                </div>
                <div class="form-group">
                  <button class="btn btn-primary" @click="runSeedAI">🚀 执行AI农资分配</button>
                </div>
              </div>
              <div ref="chartSeedAlloc" style="height: 350px"></div>
            </div>
          </div>

          <div
            class="page"
            :class="{ active: currentPage === 'seed-predict' }"
            id="page-seed-predict"
          >
            <div class="card">
              <div class="card-header">
                <div class="card-title">📈 AI农资消耗预测与库存预警算法</div>
                <span class="tag tag-info">时间序列预测模型</span>
              </div>
              <div class="chart-row">
                <div class="chart-box">
                  <div class="chart-title">📊 农资消耗趋势预测（LSTM模型</div>
                  <div ref="chartSeedPredict" style="height: 260px"></div>
                </div>
                <div class="chart-box">
                  <div class="chart-title">⚠️ 库存安全预警雷达</div>
                  <div ref="chartSeedAlert" style="height: 260px"></div>
                </div>
              </div>
              <div class="ai-panel">
                <h4>🔔 AI库存预警提示</h4>
                <p style="font-size: 13px; color: #555">
                  基于时间序列预测模型，预计下季度化肥需求量预测<strong>2,650%</strong>，当前库存
                  <strong>2,180�?</strong>，缺口
                  <strong>470台</strong>。AI建议：提前采购复合肥600吨、尿素00吨，确保春耕供应
                </p>
              </div>
            </div>
          </div>

          <div class="page" :class="{ active: currentPage === 'labor-list' }" id="page-labor-list">
            <div class="card">
              <div class="card-header">
                <div class="card-title">👷 农业劳动力资源信息库管理</div>
                <button class="btn btn-primary">+ 新增劳动力</button>
              </div>
              <table class="data-table">
                <thead>
                  <tr>
                    <th>编号</th>
                    <th>姓名</th>
                    <th>工种</th>
                    <th>技能等级</th>
                    <th>所属片区</th>
                    <th>日薪(元)</th>
                    <th>可用时段</th>
                    <th>状态</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>L001</td>
                    <td>张师傅</td>
                    <td>农机操作</td>
                    <td>高级</td>
                    <td>东片区</td>
                    <td>280</td>
                    <td>全天</td>
                    <td><span class="tag tag-success">在岗</span></td>
                  </tr>
                  <tr>
                    <td>L002</td>
                    <td>李师傅</td>
                    <td>灌溉管理</td>
                    <td>中级</td>
                    <td>西片区</td>
                    <td>220</td>
                    <td>白天</td>
                    <td><span class="tag tag-success">在岗</span></td>
                  </tr>
                  <tr>
                    <td>L003</td>
                    <td>王师傅</td>
                    <td>种植技术员</td>
                    <td>高级</td>
                    <td>南片区</td>
                    <td>260</td>
                    <td>全天</td>
                    <td><span class="tag tag-warning">请假</span></td>
                  </tr>
                  <tr>
                    <td>L004</td>
                    <td>赵师傅</td>
                    <td>设备维护</td>
                    <td>中级</td>
                    <td>北片区</td>
                    <td>240</td>
                    <td>白天</td>
                    <td><span class="tag tag-success">在岗</span></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <div
            class="page"
            :class="{ active: currentPage === 'labor-schedule' }"
            id="page-labor-schedule"
          >
            <div class="card">
              <div class="card-header">
                <div class="card-title">🤖 农忙时段人力智能排班与资源统筹</div>
                <span class="tag tag-info">粒子群优化算法</span>
              </div>
              <div class="form-row" style="margin-bottom: 16px">
                <div class="form-group">
                  <label>农时任务</label>
                  <select>
                    <option>春耕播种</option>
                    <option>夏种管理</option>
                    <option>秋收作业</option>
                    <option>冬藏整地</option>
                  </select>
                </div>
                <div class="form-group">
                  <label>任务片区</label>
                  <select>
                    <option>全部片区</option>
                    <option>东片区</option>
                    <option>西片区</option>
                  </select>
                </div>
                <div class="form-group">
                  <button class="btn btn-primary" @click="runLaborAI">🚀 生成智能排班</button>
                </div>
              </div>
              <div ref="chartLabor" style="height: 350px"></div>
            </div>
          </div>

          <div
            class="page"
            :class="{ active: currentPage === 'equipment-list' }"
            id="page-equipment-list"
          >
            <div class="card">
              <div class="card-header">
                <div class="card-title">🚜 农机具数字化台账管理</div>
                <div>
                  <button class="btn btn-primary">+ 新增设备</button>
                  <button class="btn btn-outline" style="margin-left: 8px">导出台账</button>
                </div>
              </div>
              <div class="stats-row">
                <div class="stat-card">
                  <div class="stat-value">156</div>
                  <div class="stat-label">设备总数</div>
                </div>
                <div class="stat-card success">
                  <div class="stat-value">128</div>
                  <div class="stat-label">正常运行</div>
                </div>
                <div class="stat-card warning">
                  <div class="stat-value">18</div>
                  <div class="stat-label">维护中</div>
                </div>
                <div class="stat-card danger">
                  <div class="stat-value">10</div>
                  <div class="stat-label">待维修</div>
                </div>
              </div>
              <table class="data-table">
                <thead>
                  <tr>
                    <th>设备编号</th>
                    <th>设备名称</th>
                    <th>类型</th>
                    <th>片区</th>
                    <th>作业效率</th>
                    <th>当前状态</th>
                    <th>AI健康评分</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="eq in equipmentData" :key="eq.id">
                    <td>{{ eq.id }}</td>
                    <td>{{ eq.name }}</td>
                    <td>{{ eq.type }}</td>
                    <td>{{ eq.area }}</td>
                    <td>{{ eq.eff }}</td>
                    <td>
                      <span
                        class="tag"
                        :class="
                          'tag-' +
                          (eq.status === '正常'
                            ? 'success'
                            : eq.status === '维护'
                              ? 'warning'
                              : 'danger')
                        "
                      >
                        {{ eq.status }}
                      </span>
                    </td>
                    <td>
                      <span
                        class="tag"
                        :class="
                          'tag-' +
                          (eq.score >= 80 ? 'success' : eq.score >= 60 ? 'warning' : 'danger')
                        "
                      >
                        {{ eq.score }}�?
                      </span>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <div
            class="page"
            :class="{ active: currentPage === 'equipment-allocation' }"
            id="page-equipment-allocation"
          >
            <div class="card">
              <div class="card-header">
                <div class="card-title">🔧 农机具智能调配与最短路径排序</div>
                <span class="tag tag-info">路径优化算法</span>
              </div>
              <div class="form-row" style="margin-bottom: 16px">
                <div class="form-group">
                  <label>任务类型</label>
                  <select>
                    <option>耕地作业</option>
                    <option>播种作业</option>
                    <option>收割作业</option>
                  </select>
                </div>
                <div class="form-group">
                  <label>目标地块</label>
                  <select>
                    <option>多地块协同</option>
                    <option>东片区</option>
                    <option>西片区</option>
                  </select>
                </div>
                <div class="form-group">
                  <button class="btn btn-primary" @click="runEquipmentAI">
                    🚀 执行路径优化排程
                  </button>
                </div>
              </div>
              <div class="chart-row">
                <div class="chart-box">
                  <div class="chart-title">🗺�?设备调配分布</div>
                  <div ref="chartEquipPie" style="height: 260px"></div>
                </div>
                <div class="chart-box">
                  <div class="chart-title">📊 区域作业效率对比</div>
                  <div ref="chartEquipBar" style="height: 260px"></div>
                </div>
              </div>
            </div>
          </div>

          <div
            class="page"
            :class="{ active: currentPage === 'equipment-maintenance' }"
            id="page-equipment-maintenance"
          >
            <div class="card">
              <div class="card-header">
                <div class="card-title">🔩 农机具维护保养计划管理</div>
                <button class="btn btn-primary">+ 新增维护计划</button>
              </div>
              <div class="stats-row">
                <div class="stat-card info">
                  <div class="stat-value">42</div>
                  <div class="stat-label">本月计划</div>
                </div>
                <div class="stat-card success">
                  <div class="stat-value">35</div>
                  <div class="stat-label">已完成</div>
                </div>
                <div class="stat-card warning">
                  <div class="stat-value">5</div>
                  <div class="stat-label">进行中</div>
                </div>
                <div class="stat-card danger">
                  <div class="stat-value">2</div>
                  <div class="stat-label">逾期</div>
                </div>
              </div>
              <div class="chart-row">
                <div class="chart-box">
                  <div class="chart-title">🩺 设备健康状态分析</div>
                  <div ref="chartMaintHealth" style="height: 240px"></div>
                </div>
                <div class="chart-box">
                  <div class="chart-title">💰 维护成本趋势</div>
                  <div ref="chartMaintCost" style="height: 240px"></div>
                </div>
              </div>
            </div>
          </div>

          <div
            class="page"
            :class="{ active: currentPage === 'ai-decision' }"
            id="page-ai-decision"
          >
            <div class="card">
              <div class="card-header">
                <div class="card-title">🤖 AI智能综合调配决策中心</div>
                <span class="tag tag-purple">多资源协同优化· 遗传算法</span>
              </div>
              <div
                style="
                  background: linear-gradient(135deg, #f6ffed, #e6f7ff);
                  border-radius: 8px;
                  padding: 20px;
                  margin-bottom: 16px;
                  border: 1px solid #b7eb8f;
                "
              >
                <h4 style="color: #52c41a; margin-bottom: 10px">
                  🎯 一键生成年度农业资源最优调配方案
                </h4>
                <p style="font-size: 13px; color: #555; margin-bottom: 14px">
                  整合土地、用水、农资、人力、器械五维数据，通过多约束遗传算法模型，自动生成资源协同优化方案，输出标准化决策报告
                </p>
                <div class="form-row">
                  <div class="form-group">
                    <label>优化周期</label>
                    <select>
                      <option>2025年度</option>
                      <option>2025春季</option>
                      <option>2025秋季</option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label>优化维度</label>
                    <select>
                      <option>五维全优化</option>
                      <option>土地+产能</option>
                      <option>农资+人力</option>
                    </select>
                  </div>
                  <div class="form-group">
                    <button class="btn btn-primary" @click="runAIDecision">
                      🚀 一键生成AI决策方案
                    </button>
                  </div>
                </div>
              </div>
              <div v-if="aiDecisionResultVisible" id="aiDecisionResult">
                <div class="ai-panel">
                  <h4>�?AI综合决策方案已生成（多约束遗传算法· 500代迭代· 收敛中98.6%�?</h4>
                  <div
                    style="
                      display: grid;
                      grid-template-columns: repeat(4, 1fr);
                      gap: 12px;
                      margin-top: 12px;
                    "
                  >
                    <div
                      style="
                        background: #fff;
                        padding: 12px;
                        border-radius: 6px;
                        text-align: center;
                      "
                    >
                      <div style="font-size: 20px; font-weight: 700; color: #52c41a">+12.5%</div>
                      <div style="font-size: 12px; color: #999">预计增产</div>
                    </div>
                    <div
                      style="
                        background: #fff;
                        padding: 12px;
                        border-radius: 6px;
                        text-align: center;
                      "
                    >
                      <div style="font-size: 20px; font-weight: 700; color: #1890ff">+8.3%</div>
                      <div style="font-size: 12px; color: #999">节水提升</div>
                    </div>
                    <div
                      style="
                        background: #fff;
                        padding: 12px;
                        border-radius: 6px;
                        text-align: center;
                      "
                    >
                      <div style="font-size: 20px; font-weight: 700; color: #fa8c16">+15.2%</div>
                      <div style="font-size: 12px; color: #999">增效提升</div>
                    </div>
                    <div
                      style="
                        background: #fff;
                        padding: 12px;
                        border-radius: 6px;
                        text-align: center;
                      "
                    >
                      <div style="font-size: 20px; font-weight: 700; color: #722ed1">92%</div>
                      <div style="font-size: 12px; color: #999">资源利用率</div>
                    </div>
                  </div>
                </div>
                <div class="chart-row">
                  <div class="chart-box">
                    <div class="chart-title">📊 人工调配 vs AI优化对比（雷达图）</div>
                    <div ref="chartAiRadar" style="height: 280px"></div>
                  </div>
                  <div class="chart-box">
                    <div class="chart-title">🥧 AI优化后资源分配结果</div>
                    <div ref="chartAiPie" style="height: 280px"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div
            class="page"
            :class="{ active: currentPage === 'predict-yield' }"
            id="page-predict-yield"
          >
            <div class="card">
              <div class="card-header">
                <div class="card-title">📈 作物产量预测（时间序列数据挖掘）</div>
                <span class="tag tag-info">LSTM + ARIMA混合模型</span>
              </div>
              <div class="chart-row">
                <div class="chart-box">
                  <div class="chart-title">📊 历史产量与预测趋势</div>
                  <div ref="chartYieldTrend" style="height: 260px"></div>
                </div>
                <div class="chart-box">
                  <div class="chart-title">🥧 各作物产量占比预测</div>
                  <div ref="chartYieldPie" style="height: 260px"></div>
                </div>
              </div>
            </div>
          </div>

          <div
            class="page"
            :class="{ active: currentPage === 'predict-resource' }"
            id="page-predict-resource"
          >
            <div class="card">
              <div class="card-header">
                <div class="card-title">📈 来年资源需求智能预测</div>
                <span class="tag tag-info">多元回归 + 时间序列</span>
              </div>
              <div class="chart-row">
                <div class="chart-box">
                  <div class="chart-title">💧 水资源需求预测</div>
                  <div ref="chartPredictWater" style="height: 260px"></div>
                </div>
                <div class="chart-box">
                  <div class="chart-title">🧪 农资资源需求预测</div>
                  <div ref="chartPredictSeed" style="height: 260px"></div>
                </div>
              </div>
              <div class="ai-panel">
                <h4>📋 AI资源需求预估报告（2025年度</h4>
                <p style="font-size: 13px; color: #555">
                  基于2019-2024年历史数据训练的时间序列预测模型，预测025年农业资源总需求：用水
                  <strong>2,580万m³</strong>（同期）5.3%），化肥
                  <strong>2,650%</strong>（同期）8.1%），人力
                  <strong>2.86万工时</strong
                  >（同期）3.2%）。建议提前储备水资源配额、增加化肥采购预算。
                </p>
              </div>
            </div>
          </div>

          <div class="page" :class="{ active: currentPage === 'sys-user' }" id="page-sys-user">
            <div class="card">
              <div class="card-header">
                <div class="card-title">⚙️ 系统管理 - RBAC权限与用户管理</div>
                <button class="btn btn-primary">+ 新增用户</button>
              </div>
              <div class="stats-row">
                <div class="stat-card">
                  <div class="stat-value">47</div>
                  <div class="stat-label">系统用户</div>
                </div>
                <div class="stat-card success">
                  <div class="stat-value">4</div>
                  <div class="stat-label">角色类型</div>
                </div>
                <div class="stat-card info">
                  <div class="stat-value">58</div>
                  <div class="stat-label">权限节点</div>
                </div>
                <div class="stat-card warning">
                  <div class="stat-value">1,286</div>
                  <div class="stat-label">操作日志</div>
                </div>
              </div>
              <table class="data-table">
                <thead>
                  <tr>
                    <th>用户ID</th>
                    <th>用户名</th>
                    <th>姓名</th>
                    <th>角色</th>
                    <th>部门</th>
                    <th>权限组</th>
                    <th>状态</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>U001</td>
                    <td>admin</td>
                    <td>张管理员</td>
                    <td><span class="tag tag-danger">系统管理</span></td>
                    <td>信息中心</td>
                    <td>58/58</td>
                    <td><span class="tag tag-success">正常</span></td>
                  </tr>
                  <tr>
                    <td>U002</td>
                    <td>dispatcher01</td>
                    <td>李调度员</td>
                    <td><span class="tag tag-info">生产调度</span></td>
                    <td>生产队长</td>
                    <td>36/58</td>
                    <td><span class="tag tag-success">正常</span></td>
                  </tr>
                  <tr>
                    <td>U003</td>
                    <td>farmer01</td>
                    <td>王农艺师</td>
                    <td><span class="tag tag-success">片区经理/农户</span></td>
                    <td>东片区</td>
                    <td>18/58</td>
                    <td><span class="tag tag-success">正常</span></td>
                  </tr>
                  <tr>
                    <td>U004</td>
                    <td>analyst01</td>
                    <td>赵分配员</td>
                    <td><span class="tag tag-purple">数据分析</span></td>
                    <td>数据员</td>
                    <td>24/58</td>
                    <td><span class="tag tag-success">正常</span></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { authApi, dashboardApi, farmlandApi } from '../api'

export default {
  name: 'MainSystem',
  props: {
    userInfo: {
      type: Object,
      default: () => ({
        name: '张管理员',
        role: 'admin',
        roleName: '系统管理',
      }),
    },
  },
  data() {
    return {
      currentPage: 'dashboard',
      breadcrumb: '首页',
      subBreadcrumb: '数据驾驶舱',
      showFarmlandCreateForm: false,
      farmlandList: [],
      farmlandTotal: 0,
      farmlandPage: 1,
      farmlandPageSize: 20,
      farmlandLoading: false,
      farmlandCreateForm: {
        blockCode: '',
        name: '',
        regionId: 1,
        area: null,
        soilType: '黑土',
        currentCrop: '',
        ownership: '村集体',
        plantingPeriod: '',
        status: '待播种',
        suitableCrops: '',
      },
      optimizeForm: {
        goal: 'maximizeYield',
        constraintMode: 'waterLimit',
        year: 2026,
      },
      optimizeMetrics: {
        objectiveConvergence: 0.987,
        constraintSatisfactionRate: 1,
      },
      optimizeCropDistribution: [
        { name: '水稻', percentage: 32 },
        { name: '小麦', percentage: 28 },
        { name: '玉米', percentage: 22 },
        { name: '大豆', percentage: 14 },
        { name: '其他', percentage: 4 },
      ],
      optimizeResult: null,
      optimizeBlocks: [
        {
          blockId: 101,
          blockCode: 'F001',
          name: '北区A地块',
          area: 320,
          soilType: '壤土',
          waterAvailability: 100,
          fertilityScore: 85,
        },
        {
          blockId: 102,
          blockCode: 'F002',
          name: '北区B地块',
          area: 280,
          soilType: '沙壤土',
          waterAvailability: 90,
          fertilityScore: 78,
        },
        {
          blockId: 103,
          blockCode: 'F003',
          name: '南区A地块',
          area: 260,
          soilType: '红壤',
          waterAvailability: 80,
          fertilityScore: 82,
        },
        {
          blockId: 104,
          blockCode: 'F004',
          name: '西区C地块',
          area: 300,
          soilType: '黄土',
          waterAvailability: 88,
          fertilityScore: 80,
        },
      ],
      openMenus: {
        farmland: false,
        water: false,
        seed: false,
        labor: false,
        equipment: false,
        predict: false,
      },
      charts: {},
      loadingVisible: false,
      loadingText: 'AI算法计算�?..',
      loadingDetail: '正在初始化..',
      aiOptimizeResultVisible: false,
      aiDecisionResultVisible: false,
      equipmentData: [
        {
          id: 'EQ001',
          name: '联合收割机A',
          type: '收割设备',
          area: '东区',
          eff: '15%h',
          status: '正常',
          score: 92,
        },
        {
          id: 'EQ002',
          name: '拖拉机B',
          type: '耕作设备',
          area: '西区',
          eff: '8%h',
          status: '正常',
          score: 88,
        },
        {
          id: 'EQ003',
          name: '播种机C',
          type: '播种设备',
          area: '南区',
          eff: '10%h',
          status: '维护',
          score: 72,
        },
        {
          id: 'EQ004',
          name: '喷灌设备D',
          type: '灌溉设备',
          area: '北区',
          eff: '20%h',
          status: '待修',
          score: 58,
        },
        {
          id: 'EQ005',
          name: '无人机E',
          type: '植保设备',
          area: '东区',
          eff: '50%h',
          status: '正常',
          score: 95,
        },
        {
          id: 'EQ006',
          name: '旋耕机F',
          type: '耕作设备',
          area: '西区',
          eff: '6%h',
          status: '正常',
          score: 85,
        },
        {
          id: 'EQ007',
          name: '插秧机G',
          type: '播种设备',
          area: '南区',
          eff: '4%h',
          status: '正常',
          score: 90,
        },
        {
          id: 'EQ008',
          name: '脱粒机H',
          type: '收割设备',
          area: '北区',
          eff: '12%h',
          status: '维护',
          score: 68,
        },
      ],
      pageNames: {
        dashboard: '数据驾驶舱',
        'farmland-list': '耕地地块台账',
        'farmland-optimize': 'AI种植结构优化',
        'farmland-rotation': '轮作休耕规划',
        'water-quota': '用水配额数据管理',
        'water-allocation': 'AI水量优化分配',
        'water-analysis': '用水数据分析评估',
        'seed-inventory': '农资出入库台账',
        'seed-allocation': 'AI农资按需分配',
        'seed-predict': '消耗预测与库存预警',
        'labor-list': '劳动力资源信息库',
        'labor-schedule': '智能排班调度',
        'equipment-list': '农机具台账管理',
        'equipment-allocation': '智能调配与路径优化',
        'equipment-maintenance': '维护保养管理',
        'ai-decision': 'AI综合决策中心',
        'predict-yield': '作物产量预测',
        'predict-resource': '资源需求智能预测',
        'sys-user': '系统管理（RBAC权限）',
      },
      bcMap: {
        dashboard: '首页',
        'farmland-list': '耕地资源调配',
        'farmland-optimize': '耕地资源调配',
        'farmland-rotation': '耕地资源调配',
        'water-quota': '水资源配额调度',
        'water-allocation': '水资源配额调度',
        'water-analysis': '水资源配额调度',
        'seed-inventory': '农资资源调配',
        'seed-allocation': '农资资源调配',
        'seed-predict': '农资资源调配',
        'labor-list': '人力农事资源调配',
        'labor-schedule': '人力农事资源调配',
        'equipment-list': '器械资源调配',
        'equipment-allocation': '器械资源调配',
        'equipment-maintenance': '器械资源调配',
        'ai-decision': 'AI综合决策',
        'predict-yield': '大数据分析预测',
        'predict-resource': '大数据分析预测',
        'sys-user': '系统管理',
      },
    }
  },
  mounted() {
    this.loadDashboardData()
    this.$nextTick(() => {
      this.initDashboardCharts()
    })
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    Object.values(this.charts).forEach((c) => {
      if (c && c.dispose) c.dispose()
    })
  },
  computed: {
    optimizeGoalLabel() {
      const map = {
        maximizeYield: '产量最大化',
        maximizeProfit: '利润最大化',
        minimizeRisk: '风险最小化',
        balanced: '资源均衡',
      }
      return map[this.optimizeForm.goal] || '产量最大化'
    },
    farmlandTotalPages() {
      return Math.ceil(this.farmlandTotal / this.farmlandPageSize) || 1
    },
    farmlandStats() {
      const list = this.farmlandList || []
      const totalArea = list.reduce((sum, item) => sum + (item.area || 0), 0)
      const plantedArea = list
        .filter((item) => item.status === '种植中')
        .reduce((sum, item) => sum + (item.area || 0), 0)
      const pendingArea = list
        .filter((item) => item.status === '待播种')
        .reduce((sum, item) => sum + (item.area || 0), 0)
      const fallowArea = list
        .filter((item) => item.status === '休耕')
        .reduce((sum, item) => sum + (item.area || 0), 0)
      return { totalArea, plantedArea, pendingArea, fallowArea }
    },
  },
  methods: {
    resetFarmlandCreateForm() {
      this.farmlandCreateForm = {
        blockCode: '',
        name: '',
        regionId: 1,
        area: null,
        soilType: '黑土',
        currentCrop: '',
        ownership: '村集体',
        plantingPeriod: '',
        status: '待播种',
        suitableCrops: '',
      }
    },
    async submitFarmlandCreate() {
      const form = this.farmlandCreateForm
      if (!form.blockCode || !form.name || !form.area) {
        alert('请填写地块编号、名称和面积')
        return
      }
      const payload = {
        blockCode: form.blockCode,
        name: form.name,
        regionId: form.regionId,
        area: form.area,
        soilType: form.soilType,
        suitableCrops: form.suitableCrops
          ? form.suitableCrops
              .split(',')
              .map((s) => s.trim())
              .filter(Boolean)
          : [],
        currentCrop: form.currentCrop,
        ownership: form.ownership,
        plantingPeriod: form.plantingPeriod,
        status: form.status,
      }
      try {
        await farmlandApi.create(payload)
        alert('新增地块成功！')
        this.showFarmlandCreateForm = false
        this.resetFarmlandCreateForm()
        this.loadFarmlandList()
      } catch (error) {
        alert('新增地块失败！' + (error.message || '未知错误'))
      }
    },
    buildFarmlandOptimizePayload() {
      const goalMap = {
        maximizeYield: 'maximizeYield',
        maximizeProfit: 'maximizeProfit',
        minimizeRisk: 'minimizeRisk',
        balanced: 'balanced',
      }

      return {
        year: Number(this.optimizeForm.year),
        optimizationGoal: goalMap[this.optimizeForm.goal] || 'maximizeYield',
        objectiveLabel: this.optimizeGoalLabel,
        constraints: {
          waterLimit: 1200,
          fertilizerLimit: 800,
          laborLimit: 600,
          minRiceRatio: 0.2,
          maxRiceRatio: 0.45,
          minWheatRatio: 0.15,
          maxWheatRatio: 0.35,
          minCornRatio: 0.1,
          maxCornRatio: 0.3,
          blockAllocationLimit: 0.05,
        },
      }
    },
    normalizeOptimizeResult(data) {
      const payload = data?.data || data || {}
      const cropDistribution = payload.cropDistribution || this.optimizeCropDistribution
      const blockSuitability = payload.blockSuitability || []
      const metrics = payload.optimizationMetrics || this.optimizeMetrics

      this.optimizeCropDistribution = Array.isArray(cropDistribution)
        ? cropDistribution.map((item) => ({
            name: item.name,
            percentage: Number(item.percentage || 0),
          }))
        : this.optimizeCropDistribution

      this.optimizeMetrics = {
        objectiveConvergence: Number(metrics.objectiveConvergence ?? 0.987),
        constraintSatisfactionRate: Number(metrics.constraintSatisfactionRate ?? 1),
      }

      this.optimizeResult = payload
      this.aiOptimizeResultVisible = true
      this.$nextTick(() => this.initCropCharts())
    },
    async loadDashboardData() {
      try {
        const res = await dashboardApi.getOverview()
        const data = res?.data || res
        if (data) {
          console.log('Dashboard overview loaded from backend:', data)
        }
      } catch (error) {
        console.warn('Dashboard API unavailable, using local mock data:', error)
      }
    },
    async loadFarmlandList() {
      this.farmlandLoading = true
      try {
        const res = await farmlandApi.list({
          page: this.farmlandPage,
          pageSize: this.farmlandPageSize,
        })
        const data = res?.data || res
        this.farmlandTotal = data?.total || 0
        this.farmlandList = data?.items || []
      } catch (error) {
        console.warn('Farmland list API unavailable:', error)
        this.farmlandList = []
        this.farmlandTotal = 0
      } finally {
        this.farmlandLoading = false
      }
    },
    toggleSubmenu(menu) {
      this.openMenus[menu] = !this.openMenus[menu]
    },
    switchPage(pageId) {
      this.currentPage = pageId
      this.breadcrumb = this.bcMap[pageId] || '首页'
      this.subBreadcrumb = this.pageNames[pageId] || ''
      if (pageId === 'farmland-list') {
        this.farmlandPage = 1
        this.loadFarmlandList()
      }
      this.$nextTick(() => {
        setTimeout(() => this.initChartsForPage(pageId), 100)
      })
    },
    logout() {
      if (!confirm('确定要退出登录吗？')) return
      authApi.logout().catch(() => {})
      localStorage.removeItem('agri_user')
      localStorage.removeItem('agri_token')
      localStorage.removeItem('sa_token')
      this.$emit('logout')
    },
    showLoading(text, detail) {
      this.loadingVisible = true
      this.loadingText = text || 'AI算法计算�?..'
      this.loadingDetail = detail || '正在初始化..'
    },
    hideLoading() {
      this.loadingVisible = false
    },
    handleResize() {
      Object.values(this.charts).forEach((c) => {
        if (c && c.resize) c.resize()
      })
    },
    async runAIOptimize() {
      this.showLoading('AI种植结构优化计算..', '遗传算法初始化种群（500个体）..')
      const payload = this.buildFarmlandOptimizePayload()

      try {
        const res = await farmlandApi.optimize(payload)
        const data = res?.data || res
        const result = data?.data || data || {}

        if (
          result &&
          (result.cropDistribution || result.blockSuitability || result.optimizationMetrics)
        ) {
          this.normalizeOptimizeResult(result)
          this.hideLoading()
          return
        }
      } catch (error) {
        console.warn(
          'Farmland optimization API unavailable, using local fallback mock data:',
          error,
        )
      }

      const fallback = {
        cropDistribution: [
          { name: '水稻', percentage: 38.5 },
          { name: '小麦', percentage: 27.0 },
          { name: '玉米', percentage: 24.5 },
          { name: '大豆', percentage: 10.0 },
        ],
        blockSuitability: [
          { blockId: 101, blockCode: 'F001', recommendedCrop: '水稻', suitabilityScore: 92 },
          { blockId: 102, blockCode: 'F002', recommendedCrop: '小麦', suitabilityScore: 88 },
          { blockId: 103, blockCode: 'F003', recommendedCrop: '玉米', suitabilityScore: 90 },
          { blockId: 104, blockCode: 'F004', recommendedCrop: '大豆', suitabilityScore: 85 },
        ],
        optimizationMetrics: {
          objectiveConvergence: 0.987,
          constraintSatisfactionRate: 0.96,
        },
      }

      this.normalizeOptimizeResult(fallback)
      this.hideLoading()
    },
    runWaterAI() {
      this.showLoading('AI水量优化分配计算..', '线性规划模型求解中...')
      setTimeout(() => {
        this.hideLoading()
        this.initWaterCharts()
        alert('中AI水量优化分配完成！\n\n预计节水: 8.3%\n平均增产: 9.2%')
      }, 2500)
    },
    runSeedAI() {
      this.showLoading('AI农资按需分配计算..', '多约束优化模型求解中...')
      setTimeout(() => {
        this.hideLoading()
        this.initSeedAllocChart()
        alert('�?AI农资分配完成！\n\n化肥利用率提升 12.5%\n成本降低: 8.6%')
      }, 2000)
    },
    runLaborAI() {
      this.showLoading('人力智能排班计算..', '粒子群优化算法迭代中...')
      setTimeout(() => {
        this.hideLoading()
        this.initLaborChart()
        alert('智能排班完成！\n\n整体匹配度 96.8%\n缺口: 15%')
      }, 2000)
    },
    runEquipmentAI() {
      this.showLoading('农机路径优化排程..', '最短路径算法计算中...')
      setTimeout(() => {
        this.hideLoading()
        this.initEquipmentCharts()
        alert('中路径优化排程完成！\n\n总行驶距离减少 23.5%\n作业效率提升: 18.5%')
      }, 2000)
    },
    runAIDecision() {
      this.showLoading('AI综合决策方案生成..', '五维数据融合与遗传算法运算中...')
      let steps = [
        '读取五维历史数据...',
        '构建约束条件矩阵...',
        '遗传算法500代迭代..',
        '多目标Pareto优化...',
        '生成决策报告...',
      ]
      let i = 0
      const interval = setInterval(() => {
        if (i < steps.length) {
          this.loadingDetail = steps[i]
          i++
        } else {
          clearInterval(interval)
          this.hideLoading()
          this.aiDecisionResultVisible = true
          this.$nextTick(() => this.initAIDecisionCharts())
        }
      }, 700)
    },
    initChartsForPage(pageId) {
      switch (pageId) {
        case 'dashboard':
          this.initDashboardCharts()
          break
        case 'farmland-optimize':
          this.initCropCharts()
          break
        case 'farmland-rotation':
          this.initRotationChart()
          break
        case 'water-allocation':
          this.initWaterCharts()
          break
        case 'water-analysis':
          this.initWaterAnalysisCharts()
          break
        case 'seed-allocation':
          this.initSeedAllocChart()
          break
        case 'seed-predict':
          this.initSeedPredictCharts()
          break
        case 'labor-schedule':
          this.initLaborChart()
          break
        case 'equipment-allocation':
          this.initEquipmentCharts()
          break
        case 'equipment-maintenance':
          this.initMaintenanceCharts()
          break
        case 'ai-decision':
          if (this.aiDecisionResultVisible) this.initAIDecisionCharts()
          break
        case 'predict-yield':
          this.initYieldCharts()
          break
        case 'predict-resource':
          this.initResourcePredictCharts()
          break
      }
    },
    initDashboardCharts() {
      if (this.charts['dash1']) this.charts['dash1'].dispose()
      if (this.charts['dash2']) this.charts['dash2'].dispose()
      if (this.charts['dash3']) this.charts['dash3'].dispose()
      if (!this.$refs.chartDash1) return
      const c1 = echarts.init(this.$refs.chartDash1)
      c1.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['耕地', '�?', '农资', '人力', '器械'], bottom: 0 },
        xAxis: { type: 'category', data: ['1月', '2月', '3月', '4月', '5月', '6月'] },
        yAxis: { type: 'value' },
        series: [
          {
            name: '耕地',
            type: 'bar',
            data: [3200, 3500, 3800, 4100, 4400, 4500],
            itemStyle: { color: '#52c41a' },
          },
          {
            name: '�?',
            type: 'bar',
            data: [800, 850, 900, 950, 1000, 1050],
            itemStyle: { color: '#1890ff' },
          },
          {
            name: '农资',
            type: 'bar',
            data: [200, 220, 240, 260, 280, 300],
            itemStyle: { color: '#fa8c16' },
          },
          {
            name: '人力',
            type: 'bar',
            data: [100, 110, 120, 130, 140, 150],
            itemStyle: { color: '#722ed1' },
          },
          {
            name: '器械',
            type: 'bar',
            data: [80, 90, 100, 110, 120, 130],
            itemStyle: { color: '#eb2f96' },
          },
        ],
      })
      this.charts['dash1'] = c1
      const c2 = echarts.init(this.$refs.chartDash2)
      c2.setOption({
        tooltip: { trigger: 'axis' },
        legend: { bottom: 0 },
        xAxis: { type: 'category', data: ['东片区', '西片区', '南片区', '北片区'] },
        yAxis: { type: 'value' },
        series: [
          {
            name: '耕地(亩)',
            type: 'bar',
            data: [3200, 2800, 2500, 4356],
            itemStyle: { color: '#52c41a' },
          },
          {
            name: '用水(万m³)',
            type: 'line',
            data: [800, 650, 550, 450],
            smooth: true,
            itemStyle: { color: '#1890ff' },
          },
        ],
      })
      this.charts['dash2'] = c2
      const c3 = echarts.init(this.$refs.chartDash3)
      c3.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['耕地利用率', '水资源利用率', '农资利用率', '人力利用率'], bottom: 0 },
        xAxis: { type: 'category', data: ['2020', '2021', '2022', '2023', '2024'] },
        yAxis: { type: 'value', max: 100 },
        series: [
          {
            name: '耕地利用率',
            type: 'line',
            data: [72, 75, 78, 82, 86],
            smooth: true,
            itemStyle: { color: '#52c41a' },
          },
          {
            name: '水资源利用率',
            type: 'line',
            data: [65, 68, 72, 76, 80],
            smooth: true,
            itemStyle: { color: '#1890ff' },
          },
          {
            name: '农资利用率',
            type: 'line',
            data: [60, 64, 68, 73, 78],
            smooth: true,
            itemStyle: { color: '#fa8c16' },
          },
          {
            name: '人力利用率',
            type: 'line',
            data: [70, 73, 76, 79, 83],
            smooth: true,
            itemStyle: { color: '#722ed1' },
          },
        ],
      })
      this.charts['dash3'] = c3
    },
    initCropCharts() {
      if (this.charts['crop-rose']) this.charts['crop-rose'].dispose()
      if (this.charts['crop-score']) this.charts['crop-score'].dispose()
      if (!this.$refs.chartCropRose) return

      const cropData = this.optimizeCropDistribution.length
        ? this.optimizeCropDistribution.map((item, index) => ({
            value: Number(item.percentage),
            name: `${item.name} ${item.percentage.toFixed(1)}%`,
            itemStyle: {
              color: ['#52c41a', '#faad14', '#1890ff', '#722ed1', '#eb2f96'][index % 5],
            },
          }))
        : [
            { value: 32, name: '水稻 32.0%', itemStyle: { color: '#52c41a' } },
            { value: 28, name: '小麦 28.0%', itemStyle: { color: '#faad14' } },
            { value: 22, name: '玉米 22.0%', itemStyle: { color: '#1890ff' } },
            { value: 14, name: '大豆 14.0%', itemStyle: { color: '#722ed1' } },
            { value: 4, name: '其他 4.0%', itemStyle: { color: '#eb2f96' } },
          ]

      const scoreSeries = this.optimizeResult?.blockSuitability?.length
        ? this.optimizeResult.blockSuitability.map((item) => ({
            name: item.recommendedCrop || '建议作物',
            type: 'bar',
            data: [Number(item.suitabilityScore || 0)],
            itemStyle: {
              color: ['#52c41a', '#faad14', '#1890ff', '#722ed1'][
                this.optimizeResult.blockSuitability.indexOf(item) % 4
              ],
            },
            barWidth: 18,
          }))
        : [
            {
              name: '水稻',
              type: 'bar',
              data: [92, 65, 45, 70],
              itemStyle: { color: '#52c41a' },
              barWidth: 20,
            },
            {
              name: '小麦',
              type: 'bar',
              data: [55, 88, 72, 85],
              itemStyle: { color: '#faad14' },
              barWidth: 20,
            },
            {
              name: '玉米',
              type: 'bar',
              data: [48, 75, 90, 65],
              itemStyle: { color: '#1890ff' },
              barWidth: 20,
            },
          ]

      const c1 = echarts.init(this.$refs.chartCropRose)
      c1.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        series: [
          {
            type: 'pie',
            radius: ['30%', '70%'],
            roseType: 'radius',
            itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 2 },
            data: cropData,
          },
        ],
      })
      this.charts['crop-rose'] = c1

      const c2 = echarts.init(this.$refs.chartCropScore)
      c2.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: {
          type: 'category',
          data: this.optimizeBlocks.map((item) => item.blockCode),
        },
        yAxis: { type: 'value', name: '适配度', max: 100 },
        series: scoreSeries,
      })
      this.charts['crop-score'] = c2
    },
    initRotationChart() {
      if (this.charts['rotation']) this.charts['rotation'].dispose()
      if (!this.$refs.chartRotation) return
      const c = echarts.init(this.$refs.chartRotation)
      c.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['水稻', '小麦', '玉米', '休耕'], bottom: 0 },
        xAxis: { type: 'category', data: ['2022', '2023', '2024', '2025'] },
        yAxis: { type: 'value', name: '亩' },
        series: [
          {
            name: '水稻',
            type: 'bar',
            stack: 'a',
            data: [3200, 3300, 3400, 4114],
            itemStyle: { color: '#52c41a' },
          },
          {
            name: '小麦',
            type: 'bar',
            stack: 'a',
            data: [2800, 2900, 3000, 3600],
            itemStyle: { color: '#faad14' },
          },
          {
            name: '玉米',
            type: 'bar',
            stack: 'a',
            data: [2500, 2400, 2300, 2828],
            itemStyle: { color: '#1890ff' },
          },
          {
            name: '休耕',
            type: 'bar',
            stack: 'a',
            data: [1500, 1400, 1500, 1300],
            itemStyle: { color: '#bfbfbf' },
          },
        ],
      })
      this.charts['rotation'] = c
    },
    initWaterCharts() {
      if (this.charts['water-compare']) this.charts['water-compare'].dispose()
      if (this.charts['water-radar']) this.charts['water-radar'].dispose()
      if (!this.$refs.chartWaterCompare) return
      const c1 = echarts.init(this.$refs.chartWaterCompare)
      c1.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['传统方案', 'AI优化方案'], bottom: 0 },
        xAxis: { type: 'category', data: ['东片区', '西片区', '南片区', '北片区'] },
        yAxis: { type: 'value', name: '万m³' },
        series: [
          {
            name: '传统方案',
            type: 'bar',
            data: [850, 720, 600, 500],
            itemStyle: { color: '#faad14' },
          },
          {
            name: 'AI优化方案',
            type: 'bar',
            data: [800, 650, 550, 450],
            itemStyle: { color: '#52c41a' },
          },
        ],
      })
      this.charts['water-compare'] = c1
      const c2 = echarts.init(this.$refs.chartWaterRadar)
      c2.setOption({
        tooltip: {},
        legend: { data: ['当前效率', 'AI优化'], bottom: 0 },
        radar: {
          indicator: [
            { name: '节水灌溉', max: 100 },
            { name: '产量保障', max: 100 },
            { name: '均衡性', max: 100 },
            { name: '利用率', max: 100 },
            { name: '可持续性', max: 100 },
          ],
          radius: '60%',
        },
        series: [
          {
            type: 'radar',
            data: [
              {
                value: [65, 78, 55, 72, 60],
                name: '当前效率',
                itemStyle: { color: '#faad14' },
                areaStyle: { opacity: 0.2 },
              },
              {
                value: [88, 92, 85, 90, 87],
                name: 'AI优化',
                itemStyle: { color: '#52c41a' },
                areaStyle: { opacity: 0.3 },
              },
            ],
          },
        ],
      })
      this.charts['water-radar'] = c2
    },
    initWaterAnalysisCharts() {
      if (this.charts['water-trend']) this.charts['water-trend'].dispose()
      if (this.charts['water-waste']) this.charts['water-waste'].dispose()
      if (!this.$refs.chartWaterTrend) return
      const c1 = echarts.init(this.$refs.chartWaterTrend)
      c1.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['实际用水', '预测趋势'], bottom: 0 },
        xAxis: {
          type: 'category',
          data: ['2019', '2020', '2021', '2022', '2023', '2024', '2025E'],
        },
        yAxis: { type: 'value', name: '万m³' },
        series: [
          {
            name: '实际用水',
            type: 'line',
            data: [2100, 2200, 2280, 2350, 2400, 2450, null],
            itemStyle: { color: '#1890ff' },
            smooth: true,
          },
          {
            name: '预测趋势',
            type: 'line',
            data: [null, null, null, null, null, 2450, 2580],
            itemStyle: { color: '#52c41a' },
            lineStyle: { type: 'dashed' },
            smooth: true,
          },
        ],
      })
      this.charts['water-trend'] = c1
      const c2 = echarts.init(this.$refs.chartWaterWaste)
      c2.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        series: [
          {
            type: 'pie',
            radius: ['40%', '70%'],
            data: [
              { value: 82, name: '高效利用', itemStyle: { color: '#52c41a' } },
              { value: 12, name: '轻微浪费', itemStyle: { color: '#faad14' } },
              { value: 6, name: '严重浪费', itemStyle: { color: '#f5222d' } },
            ],
          },
        ],
      })
      this.charts['water-waste'] = c2
    },
    initSeedAllocChart() {
      if (this.charts['seed-alloc']) this.charts['seed-alloc'].dispose()
      if (!this.$refs.chartSeedAlloc) return
      const c = echarts.init(this.$refs.chartSeedAlloc)
      c.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['水稻', '小麦', '玉米', '大豆'], bottom: 0 },
        xAxis: { type: 'category', data: ['复合肥', '尿素', '杀虫剂', '种子'] },
        yAxis: { type: 'value', name: 'kg' },
        series: [
          {
            name: '水稻',
            type: 'bar',
            stack: 'a',
            data: [850, 620, 25, 35],
            itemStyle: { color: '#52c41a' },
          },
          {
            name: '小麦',
            type: 'bar',
            stack: 'a',
            data: [720, 540, 18, 28],
            itemStyle: { color: '#faad14' },
          },
          {
            name: '玉米',
            type: 'bar',
            stack: 'a',
            data: [380, 280, 12, 15],
            itemStyle: { color: '#1890ff' },
          },
          {
            name: '大豆',
            type: 'bar',
            stack: 'a',
            data: [230, 120, 9, 7],
            itemStyle: { color: '#722ed1' },
          },
        ],
      })
      this.charts['seed-alloc'] = c
    },
    initSeedPredictCharts() {
      if (this.charts['seed-predict']) this.charts['seed-predict'].dispose()
      if (this.charts['seed-alert']) this.charts['seed-alert'].dispose()
      if (!this.$refs.chartSeedPredict) return
      const c1 = echarts.init(this.$refs.chartSeedPredict)
      c1.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['历史消耗', 'AI预测'], bottom: 0 },
        xAxis: { type: 'category', data: ['Q1', 'Q2', 'Q3', 'Q4'] },
        yAxis: { type: 'value', name: 'kg' },
        series: [
          {
            name: '历史消耗',
            type: 'line',
            data: [2100, 2300, 2450, 2600],
            itemStyle: { color: '#1890ff' },
            smooth: true,
          },
          {
            name: 'AI预测',
            type: 'line',
            data: [2200, 2450, 2650, 2650],
            itemStyle: { color: '#52c41a' },
            lineStyle: { type: 'dashed' },
            smooth: true,
          },
        ],
      })
      this.charts['seed-predict'] = c1
      const c2 = echarts.init(this.$refs.chartSeedAlert)
      c2.setOption({
        tooltip: { trigger: 'axis' },
        radar: {
          indicator: [
            { name: '复合肥', max: 100 },
            { name: '尿素', max: 100 },
            { name: '杀虫剂', max: 100 },
            { name: '种子', max: 100 },
            { name: '有机肥', max: 100 },
          ],
          radius: '55%',
        },
        series: [
          {
            type: 'radar',
            data: [
              {
                value: [75, 68, 35, 82, 60],
                name: '当前库存安全阈值',
                itemStyle: { color: '#1890ff' },
                areaStyle: { opacity: 0.2 },
              },
            ],
          },
        ],
      })
      this.charts['seed-alert'] = c2
    },
    initLaborChart() {
      if (this.charts['labor']) this.charts['labor'].dispose()
      if (!this.$refs.chartLabor) return
      const c = echarts.init(this.$refs.chartLabor)
      c.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['需求人数', '供给人数', '缺口'], bottom: 0 },
        xAxis: { type: 'category', data: ['春耕播种', '夏种管理', '秋收作业', '冬藏整地'] },
        yAxis: { type: 'value', name: '人' },
        series: [
          {
            name: '需求人数',
            type: 'bar',
            data: [1200, 980, 1500, 600],
            itemStyle: { color: '#fa8c16' },
          },
          {
            name: '供给人数',
            type: 'bar',
            data: [1100, 920, 1450, 580],
            itemStyle: { color: '#52c41a' },
          },
          { name: '缺口', type: 'bar', data: [100, 60, 50, 20], itemStyle: { color: '#f5222d' } },
        ],
      })
      this.charts['labor'] = c
    },
    initEquipmentCharts() {
      if (this.charts['equip-pie']) this.charts['equip-pie'].dispose()
      if (this.charts['equip-bar']) this.charts['equip-bar'].dispose()
      if (!this.$refs.chartEquipPie) return
      const c1 = echarts.init(this.$refs.chartEquipPie)
      c1.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        series: [
          {
            type: 'pie',
            radius: ['40%', '70%'],
            data: [
              { value: 45, name: '收割设备', itemStyle: { color: '#52c41a' } },
              { value: 40, name: '耕作设备', itemStyle: { color: '#1890ff' } },
              { value: 33, name: '播种设备', itemStyle: { color: '#faad14' } },
              { value: 32, name: '灌溉设备', itemStyle: { color: '#722ed1' } },
              { value: 12, name: '植保设备', itemStyle: { color: '#eb2f96' } },
            ],
          },
        ],
      })
      this.charts['equip-pie'] = c1
      const c2 = echarts.init(this.$refs.chartEquipBar)
      c2.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: ['东区', '西区', '南区', '北区'] },
        yAxis: { type: 'value', name: '利用率%)', max: 100 },
        series: [
          {
            type: 'bar',
            data: [92, 88, 85, 78],
            itemStyle: { color: '#52c41a' },
            barWidth: 30,
            label: { show: true, position: 'top', formatter: '{c}%' },
          },
        ],
      })
      this.charts['equip-bar'] = c2
    },
    initMaintenanceCharts() {
      if (this.charts['maint-health']) this.charts['maint-health'].dispose()
      if (this.charts['maint-cost']) this.charts['maint-cost'].dispose()
      if (!this.$refs.chartMaintHealth) return
      const c1 = echarts.init(this.$refs.chartMaintHealth)
      c1.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        series: [
          {
            type: 'pie',
            radius: '60%',
            data: [
              { value: 82, name: '健康', itemStyle: { color: '#52c41a' } },
              { value: 12, name: '亚健康', itemStyle: { color: '#faad14' } },
              { value: 6, name: '需维护', itemStyle: { color: '#f5222d' } },
            ],
          },
        ],
      })
      this.charts['maint-health'] = c1
      const c2 = echarts.init(this.$refs.chartMaintCost)
      c2.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: ['1月', '2月', '3月', '4月', '5月', '6月'] },
        yAxis: { type: 'value', name: '万元' },
        series: [
          {
            type: 'line',
            data: [3.2, 2.8, 4.5, 3.6, 5.2, 4.8],
            itemStyle: { color: '#1890ff' },
            smooth: true,
            areaStyle: { opacity: 0.2 },
          },
        ],
      })
      this.charts['maint-cost'] = c2
    },
    initAIDecisionCharts() {
      if (this.charts['ai-radar']) this.charts['ai-radar'].dispose()
      if (this.charts['ai-pie']) this.charts['ai-pie'].dispose()
      if (!this.$refs.chartAiRadar) return
      const c1 = echarts.init(this.$refs.chartAiRadar)
      c1.setOption({
        tooltip: {},
        legend: { data: ['人工调配', 'AI优化'], bottom: 0 },
        radar: {
          indicator: [
            { name: '产量效益', max: 100 },
            { name: '节水能力', max: 100 },
            { name: '农资效率', max: 100 },
            { name: '人力利用', max: 100 },
            { name: '土壤保护', max: 100 },
            { name: '风险控制', max: 100 },
          ],
          radius: '55%',
        },
        series: [
          {
            type: 'radar',
            data: [
              {
                value: [75, 60, 70, 65, 72, 68],
                name: '人工调配',
                itemStyle: { color: '#faad14' },
                areaStyle: { opacity: 0.2 },
              },
              {
                value: [92, 88, 85, 82, 90, 91],
                name: 'AI优化',
                itemStyle: { color: '#52c41a' },
                areaStyle: { opacity: 0.3 },
              },
            ],
          },
        ],
      })
      this.charts['ai-radar'] = c1
      const c2 = echarts.init(this.$refs.chartAiPie)
      c2.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        series: [
          {
            type: 'pie',
            radius: ['35%', '65%'],
            roseType: 'radius',
            data: [
              { value: 32, name: '耕地优化', itemStyle: { color: '#52c41a' } },
              { value: 25, name: '水资源', itemStyle: { color: '#1890ff' } },
              { value: 20, name: '农资分配', itemStyle: { color: '#faad14' } },
              { value: 15, name: '人力调度', itemStyle: { color: '#722ed1' } },
              { value: 8, name: '器械调配', itemStyle: { color: '#eb2f96' } },
            ],
          },
        ],
      })
      this.charts['ai-pie'] = c2
    },
    initYieldCharts() {
      if (this.charts['yield-trend']) this.charts['yield-trend'].dispose()
      if (this.charts['yield-pie']) this.charts['yield-pie'].dispose()
      if (!this.$refs.chartYieldTrend) return
      const c1 = echarts.init(this.$refs.chartYieldTrend)
      c1.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['水稻', '小麦', '玉米'], bottom: 0 },
        xAxis: { type: 'category', data: ['2020', '2021', '2022', '2023', '2024', '2025E'] },
        yAxis: { type: 'value', name: '产量(吨/亩)' },
        series: [
          {
            name: '水稻',
            type: 'line',
            data: [3200, 3350, 3450, 3580, 3680, 3850],
            smooth: true,
            itemStyle: { color: '#52c41a' },
          },
          {
            name: '小麦',
            type: 'line',
            data: [2100, 2180, 2250, 2320, 2380, 2480],
            smooth: true,
            itemStyle: { color: '#faad14' },
          },
          {
            name: '玉米',
            type: 'line',
            data: [2800, 2920, 3050, 3180, 3280, 3400],
            smooth: true,
            itemStyle: { color: '#1890ff' },
          },
        ],
      })
      this.charts['yield-trend'] = c1
      const c2 = echarts.init(this.$refs.chartYieldPie)
      c2.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        series: [
          {
            type: 'pie',
            radius: '60%',
            data: [
              { value: 3850, name: '水稻 42%', itemStyle: { color: '#52c41a' } },
              { value: 2480, name: '小麦 27%', itemStyle: { color: '#faad14' } },
              { value: 3400, name: '玉米 31%', itemStyle: { color: '#1890ff' } },
            ],
          },
        ],
      })
      this.charts['yield-pie'] = c2
    },
    initResourcePredictCharts() {
      if (this.charts['predict-water']) this.charts['predict-water'].dispose()
      if (this.charts['predict-seed']) this.charts['predict-seed'].dispose()
      if (!this.$refs.chartPredictWater) return
      const c1 = echarts.init(this.$refs.chartPredictWater)
      c1.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['农业用水', '生态用水'], bottom: 0 },
        xAxis: { type: 'category', data: ['2024', '2025E', '2026E', '2027E'] },
        yAxis: { type: 'value', name: '万m³' },
        series: [
          {
            name: '农业用水',
            type: 'line',
            data: [2450, 2580, 2650, 2720],
            smooth: true,
            areaStyle: { opacity: 0.2 },
            itemStyle: { color: '#52c41a' },
          },
          {
            name: '生态用水',
            type: 'line',
            data: [320, 350, 380, 410],
            smooth: true,
            areaStyle: { opacity: 0.2 },
            itemStyle: { color: '#1890ff' },
          },
        ],
      })
      this.charts['predict-water'] = c1
      const c2 = echarts.init(this.$refs.chartPredictSeed)
      c2.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['化肥', '农药', '种子'], bottom: 0 },
        xAxis: { type: 'category', data: ['2024', '2025E', '2026E', '2027E'] },
        yAxis: { type: 'value', name: 'kg' },
        series: [
          {
            name: '化肥',
            type: 'bar',
            data: [2180, 2350, 2480, 2600],
            itemStyle: { color: '#52c41a' },
            barWidth: 20,
          },
          {
            name: '农药',
            type: 'bar',
            data: [64, 58, 52, 48],
            itemStyle: { color: '#faad14' },
            barWidth: 20,
          },
          {
            name: '种子',
            type: 'bar',
            data: [320, 340, 355, 370],
            itemStyle: { color: '#1890ff' },
            barWidth: 20,
          },
        ],
      })
      this.charts['predict-seed'] = c2
    },
  },
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.main-system {
  font-family:
    'Microsoft YaHei',
    -apple-system,
    sans-serif;
  background: #f0f2f5;
  color: #333;
  height: 100vh;
}

.layout {
  display: flex;
  height: 100vh;
  width: 100%;
}

.sidebar {
  width: 240px;
  background: #001529;
  color: #fff;
  flex-shrink: 0;
  overflow-y: auto;
}

.logo {
  padding: 16px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.logo h2 {
  font-size: 14px;
  background: linear-gradient(90deg, #52c41a, #1890ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.logo p {
  font-size: 10px;
  color: rgba(255, 255, 255, 0.4);
  margin-top: 4px;
}

.menu {
  padding: 8px 0;
}

.menu-item {
  padding: 11px 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  font-size: 13px;
  border-left: 3px solid transparent;
  transition: all 0.3s;
  color: rgba(255, 255, 255, 0.75);
}

.menu-item:hover {
  background: rgba(24, 144, 255, 0.1);
  color: #1890ff;
}

.menu-item.active {
  background: rgba(82, 196, 26, 0.15);
  color: #52c41a;
  border-left-color: #52c41a;
}

.menu-item .icon {
  margin-right: 10px;
  font-size: 15px;
}

.submenu {
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.3s;
  background: rgba(0, 0, 0, 0.2);
}

.submenu.open {
  max-height: 300px;
}

.submenu-item {
  padding: 9px 18px 9px 48px;
  cursor: pointer;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
}

.submenu-item:hover {
  color: #52c41a;
  background: rgba(82, 196, 26, 0.08);
}

.submenu-item.active {
  color: #52c41a;
}

.main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  height: 56px;
  background: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.breadcrumb {
  font-size: 13px;
  color: #666;
}

.breadcrumb span:first-child {
  color: #1890ff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #52c41a, #1890ff);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 12px;
  font-weight: bold;
}

.user-name {
  font-size: 13px;
}

.role-tag {
  display: inline-block;
  padding: 1px 6px;
  border-radius: 10px;
  font-size: 10px;
  margin-left: 4px;
}

.role-tag.admin {
  background: #fff1f0;
  color: #f5222d;
}
.role-tag.dispatcher {
  background: #e6f7ff;
  color: #1890ff;
}
.role-tag.farmer {
  background: #f6ffed;
  color: #52c41a;
}
.role-tag.analyst {
  background: #f9f0ff;
  color: #722ed1;
}

.logout-btn {
  padding: 5px 12px;
  border: 1px solid #d9d9d9;
  background: #fff;
  color: #666;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.logout-btn:hover {
  border-color: #ff4d4f;
  color: #ff4d4f;
}

.content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background: #f0f2f5;
}

.page {
  display: none;
}

.page.active {
  display: block;
}

.card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 14px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
}

.tag-success {
  background: #f6ffed;
  color: #52c41a;
}
.tag-warning {
  background: #fffbe6;
  color: #faad14;
}
.tag-danger {
  background: #fff1f0;
  color: #f5222d;
}
.tag-info {
  background: #e6f7ff;
  color: #1890ff;
}
.tag-purple {
  background: #f9f0ff;
  color: #722ed1;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  margin-bottom: 14px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
  border-left: 3px solid #52c41a;
}

.stat-card.warning {
  border-left-color: #faad14;
}
.stat-card.danger {
  border-left-color: #f5222d;
}
.stat-card.info {
  border-left-color: #1890ff;
}
.stat-card.purple {
  border-left-color: #722ed1;
}
.stat-card.success {
  border-left-color: #52c41a;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

.chart-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
  margin-bottom: 14px;
}

.chart-box {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}

.chart-title {
  font-size: 13px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
}

.data-table th {
  background: #fafafa;
  padding: 10px;
  text-align: left;
  font-weight: 600;
  border-bottom: 1px solid #f0f0f0;
}

.data-table td {
  padding: 10px;
  border-bottom: 1px solid #f0f0f0;
  color: #555;
}

.data-table tr:hover {
  background: #fafafa;
}

.btn {
  padding: 5px 12px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s;
}

.btn-primary {
  background: #52c41a;
  color: #fff;
}

.btn-primary:hover {
  background: #389e0d;
}

.btn-outline {
  background: #fff;
  color: #666;
  border: 1px solid #d9d9d9;
}

.btn-sm {
  padding: 3px 8px;
  border: 1px solid #d9d9d9;
  background: #fff;
  border-radius: 3px;
  cursor: pointer;
  font-size: 11px;
}

.form-row {
  display: flex;
  gap: 14px;
  align-items: flex-end;
  flex-wrap: wrap;
}

.form-row .form-group {
  flex: 1;
  min-width: 140px;
}

.form-group {
  margin-bottom: 0;
}

.form-group label {
  display: block;
  font-size: 12px;
  color: #666;
  margin-bottom: 6px;
}

.form-row select {
  width: 100%;
  padding: 7px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 12px;
}

.dash-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  margin-bottom: 14px;
}

.dash-stat {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 14px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}

.dash-stat .icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.dash-stat .icon.green {
  background: #f6ffed;
  color: #52c41a;
}
.dash-stat .icon.blue {
  background: #e6f7ff;
  color: #1890ff;
}
.dash-stat .icon.orange {
  background: #fff7e6;
  color: #fa8c16;
}
.dash-stat .icon.purple {
  background: #f9f0ff;
  color: #722ed1;
}

.dash-stat h3 {
  font-size: 20px;
  margin-bottom: 2px;
}

.dash-stat p {
  font-size: 12px;
  color: #999;
}

.ai-panel {
  background: linear-gradient(135deg, #f6ffed, #e6f7ff);
  border: 1px solid #b7eb8f;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 14px;
}

.ai-panel h4 {
  color: #52c41a;
  font-size: 14px;
  margin-bottom: 8px;
}

.progress-item {
  margin-bottom: 10px;
}

.progress-label {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  margin-bottom: 4px;
}

.progress-bar {
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #52c41a, #1890ff);
  border-radius: 4px;
  transition: width 0.5s;
}

.loading-overlay {
  display: none;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  z-index: 9999;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}

.loading-overlay.active {
  display: flex;
}

.spinner {
  width: 48px;
  height: 48px;
  border: 4px solid #f0f0f0;
  border-top-color: #52c41a;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: 14px;
  color: #666;
}

.loading-detail {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}

.main-system.role-farmer .admin-only,
.main-system.role-farmer .dispatcher-only {
  display: none !important;
}
.main-system.role-analyst .admin-only,
.main-system.role-analyst .dispatcher-only {
  display: none !important;
}
.main-system.role-dispatcher .admin-only {
  display: none !important;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  font-size: 13px;
  color: #666;
}
.pagination .btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}
</style>
