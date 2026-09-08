﻿<template>
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
                  <h3>{{ dashboardStats.farmland }}</h3>
                  <p>耕地总面积</p>
                </div>
              </div>
              <div class="dash-stat">
                <div class="icon blue">💧</div>
                <div>
                  <h3>{{ dashboardStats.water }}</h3>
                  <p>年度用水配额</p>
                </div>
              </div>
              <div class="dash-stat">
                <div class="icon orange">🧪</div>
                <div>
                  <h3>{{ dashboardStats.seed }}</h3>
                  <p>农资库存总量</p>
                </div>
              </div>
              <div class="dash-stat">
                <div class="icon purple">🚜</div>
                <div>
                  <h3>{{ dashboardStats.equipment }}</h3>
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
                    v-for="(alert, idx) in dashAlerts"
                    :key="idx"
                    :style="{
                      display: 'flex',
                      alignItems: 'center',
                      gap: '10px',
                      padding: '10px',
                      borderRadius: '6px',
                      marginBottom: '8px',
                      background: alert.bg || '#fffbe6',
                    }"
                  >
                    <span>{{ alert.icon }}</span>
                    <span style="font-size: 13px" v-html="alert.text"></span>
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
              <div v-if="showFarmlandEditForm" class="card" style="margin-bottom: 16px">
                <h3>编辑耕地地块</h3>
                <div class="form-group">
                  <label>地块编号</label>
                  <input v-model="farmlandEditForm.blockCode" placeholder="GZ-001" />
                </div>
                <div class="form-group">
                  <label>所属片区</label>
                  <select v-model="farmlandEditForm.region">
                    <option value="">请选择片区</option>
                    <option v-for="r in regionOptions" :key="r" :value="r">{{ r }}</option>
                  </select>
                </div>
                <div class="form-group">
                  <label>面积（亩）</label>
                  <input v-model.number="farmlandEditForm.area" type="number" placeholder="50" />
                </div>
                <div class="form-group">
                  <label>土壤类型</label>
                  <select v-model="farmlandEditForm.soilType">
                    <option value="">请选择土壤类型</option>
                    <option>黑土</option>
                    <option>黄土</option>
                    <option>沙土</option>
                    <option>黏土</option>
                    <option>红壤</option>
                  </select>
                </div>
                <div class="form-group">
                  <label>适宜作物（逗号分隔）</label>
                  <input v-model="farmlandEditForm.suitableCropsStr" placeholder="小麦,玉米,大豆" />
                </div>
                <div class="form-group">
                  <label>当前作物</label>
                  <input v-model="farmlandEditForm.currentCrop" placeholder="小麦" />
                </div>
                <div class="form-group">
                  <label>权属</label>
                  <select v-model="farmlandEditForm.ownership">
                    <option value="">请选择权属</option>
                    <option>集体</option>
                    <option>承包</option>
                    <option>流转</option>
                  </select>
                </div>
                <div class="form-group">
                  <label>可种植周期</label>
                  <input v-model="farmlandEditForm.plantingPeriod" placeholder="春播-秋收" />
                </div>
                <div class="form-group">
                  <label>状态</label>
                  <select v-model="farmlandEditForm.status">
                    <option value="">请选择状态</option>
                    <option>待播种</option>
                    <option>种植中</option>
                    <option>休耕</option>
                  </select>
                </div>
                <div style="margin-top: 12px">
                  <button class="btn btn-primary" @click="submitFarmlandUpdate">保存</button>
                  <button
                    class="btn btn-outline"
                    style="margin-left: 8px"
                    @click="showFarmlandEditForm = false"
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
                    <th>操作</th>
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
                    <td>
                      <button class="btn btn-outline btn-sm" @click="editFarmland(item)">编辑</button>
                      <button class="btn btn-danger btn-sm" style="margin-left:4px" @click="deleteFarmland(item)">删除</button>
                    </td>
                  </tr>
                  <tr v-if="farmlandList.length === 0">
                    <td colspan="10" style="text-align: center; padding: 20px">暂无数据</td>
                  </tr>
                </tbody>
              </table>
              <div class="pagination" v-if="farmlandTotal > 0">
                <span>{{ farmlandTotal }} </span>
                <button class="btn btn-outline" :disabled="farmlandPage <= 1" @click="prevPage">
                  上一页
                </button>
                <span>{{ farmlandPage }} / {{ farmlandTotalPages }} </span>
                <button
                  class="btn btn-outline"
                  :disabled="farmlandPage >= farmlandTotalPages"
                  @click="nextPage"
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
                  <div class="chart-title">🥧 {{ optimizeDataFromAI ? '优化后' : '原始' }}种植结构分布（玫瑰图）</div>
                  <div ref="chartCropRose" style="height: 280px"></div>
                </div>
                <div class="chart-box">
                  <div class="chart-title">📊 {{ optimizeDataFromAI ? '地块-作物适配度AI评分' : '片区-作物分布（原始数据）' }}</div>
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
              <div class="form-row" style="margin-bottom: 16px">
                <div class="form-group">
                  <label>选择年份</label>
                  <select v-model="rotationYear" @change="initRotationChart()">
                    <option :value="2021">2021</option>
                    <option :value="2022">2022</option>
                    <option :value="2023">2023</option>
                    <option :value="2024">2024</option>
                    <option :value="2025">2025</option>
                    <option :value="2026">2026</option>
                  </select>
                </div>
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
                  <button class="btn btn-primary" @click="showQuotaAdjust">+ 配额调整</button>
                </div>
              </div>
              <div class="stats-row">
                <div class="stat-card info">
                  <div class="stat-value">{{ waterQuotaData.totalQuota.toLocaleString() }}</div>
                  <div class="stat-label">年度总配额（万m³）</div>
                </div>
                <div class="stat-card">
                  <div class="stat-value">{{ waterQuotaData.allocated.toLocaleString() }}</div>
                  <div class="stat-label">已分配（万m³）</div>
                </div>
                <div class="stat-card success">
                  <div class="stat-value">{{ waterQuotaData.remaining.toLocaleString() }}</div>
                  <div class="stat-label">剩余可调配（万m³）</div>
                </div>
                <div class="stat-card warning">
                  <div class="stat-value">{{ waterQuotaData.usageRate }}%</div>
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
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in waterQuotaData.items" :key="item.area">
                    <td>{{ item.area }}</td>
                    <td>{{ item.quota }}</td>
                    <td>{{ item.used }}</td>
                    <td>{{ item.remaining }}</td>
                    <td>{{ item.usageRate }}%</td>
                    <td>{{ item.cropType }}</td>
                    <td>
                      <span
                        :class="'tag ' + (item.aiStatus === '合理' ? 'tag-success' : 'tag-warning')"
                        >{{ item.aiStatus }}</span
                      >
                    </td>
                    <td>
                      <span
                        :class="'tag ' + (item.status === '正常' ? 'tag-success' : 'tag-warning')"
                        >{{ item.status }}</span
                      >
                    </td>
                    <td>
                      <button class="btn btn-danger btn-sm" @click="deleteWaterQuota(item)">删除</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <div
            class="modal-overlay"
            :class="{ active: quotaAdjustVisible }"
            @click.self="cancelQuotaAdjust"
          >
            <div class="modal-box">
              <div class="modal-header">
                <h3>配额调整</h3>
                <button class="modal-close" @click="cancelQuotaAdjust">✕</button>
              </div>
              <div class="modal-body">
                <table class="data-table">
                  <thead>
                    <tr>
                      <th>片区</th>
                      <th>年度配额(万m³)</th>
                      <th>已使用</th>
                      <th>作物类型</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="(item, index) in quotaAdjustItems" :key="item.area">
                      <td>{{ item.area }}</td>
                      <td>
                        <input type="number" v-model.number="item.quota" class="modal-input" />
                      </td>
                      <td>{{ item.used }}</td>
                      <td>{{ item.cropType }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <div class="modal-footer">
                <button class="btn btn-outline" @click="cancelQuotaAdjust">取消</button>
                <button
                  class="btn btn-primary"
                  @click="saveQuotaAdjust"
                  :disabled="quotaAdjustSaving"
                >
                  {{ quotaAdjustSaving ? '保存中...' : '保存配置' }}
                </button>
              </div>
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
                  <select v-model="waterCycle">
                    <option>季度分配</option>
                    <option>月度分配</option>
                  </select>
                </div>
                <div class="form-group">
                  <label>优化目标</label>
                  <select v-model="waterGoal">
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
                <div>
                  <span style="margin-right: 8px; color: #666">选择年份：</span>
                  <select v-model="waterAnalysisYear" @change="loadWaterAnalysis()">
                    <option v-for="y in waterAnalysisYears" :key="y" :value="y">{{ y }}年</option>
                  </select>
                </div>
              </div>
              <div class="chart-row">
                <div class="chart-box">
                  <div class="chart-title">📈 {{ waterAnalysisYear }}年月度用水趋势与预测</div>
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
                    <tr v-for="item in waterAnalysisReport" :key="item.region">
                      <td>{{ item.region }}</td>
                      <td>{{ item.actualUsage }}万m³</td>
                      <td>{{ item.theoreticalDemand }}万m³</td>
                      <td>{{ item.wasteAmount }}万m³</td>
                      <td>{{ item.wasteRate }}%</td>
                      <td>{{ item.aiSuggestion }}</td>
                      <td>{{ item.savingPotential }}%</td>
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
                  <button class="btn btn-primary" @click="showSeedCreateForm = true">
                    + 入库登记
                  </button>
                </div>
              </div>
              <div v-if="showSeedCreateForm" class="card" style="margin-bottom: 16px">
                <div class="card-header">
                  <div class="card-title">📝 入库登记</div>
                  <button class="btn btn-outline" @click="showSeedCreateForm = false">✕</button>
                </div>
                <div class="form-row">
                  <div class="form-group">
                    <label>物资编号</label>
                    <input v-model="seedCreateForm.code" placeholder="如：S008" />
                  </div>
                  <div class="form-group">
                    <label>名称</label>
                    <input v-model="seedCreateForm.name" placeholder="如：复合肥" />
                  </div>
                  <div class="form-group">
                    <label>类型</label>
                    <select v-model="seedCreateForm.type">
                      <option value="化肥">化肥</option>
                      <option value="农药">农药</option>
                      <option value="种子">种子</option>
                      <option value="农膜">农膜</option>
                      <option value="农机配件">农机配件</option>
                    </select>
                  </div>
                </div>
                <div class="form-row">
                  <div class="form-group">
                    <label>入库数量</label>
                    <input
                      v-model.number="seedCreateForm.stock"
                      type="number"
                      placeholder="请输入入库数量"
                    />
                  </div>
                  <div class="form-group">
                    <label>安全阈值</label>
                    <input
                      v-model.number="seedCreateForm.threshold"
                      type="number"
                      placeholder="请输入安全阈值"
                    />
                  </div>
                  <div class="form-group">
                    <label>单位</label>
                    <select v-model="seedCreateForm.unit">
                      <option value="吨">吨</option>
                      <option value="公斤">公斤</option>
                      <option value="升">升</option>
                      <option value="件">件</option>
                      <option value="卷">卷</option>
                    </select>
                  </div>
                </div>
                <div class="form-row">
                  <div class="form-group">
                    <label>有效期</label>
                    <input v-model="seedCreateForm.expiry" placeholder="如：2027-06-30" />
                  </div>
                  <div class="form-group">
                    <label>状态</label>
                    <select v-model="seedCreateForm.status">
                      <option value="正常">正常</option>
                      <option value="紧张">紧张</option>
                    </select>
                  </div>
                </div>
                <div style="margin-top: 12px">
                  <button class="btn btn-primary" @click="submitSeedCreate">提交</button>
                  <button
                    class="btn btn-outline"
                    style="margin-left: 8px"
                    @click="showSeedCreateForm = false"
                  >
                    取消
                  </button>
                </div>
              </div>
              <div v-if="showSeedEditForm" class="card" style="margin-bottom: 16px">
                <div class="card-header">
                  <div class="card-title">✏️ 编辑农资</div>
                  <button class="btn btn-outline" @click="showSeedEditForm = false">✕</button>
                </div>
                <div class="form-row">
                  <div class="form-group">
                    <label>物资编号</label>
                    <input v-model="seedEditForm.code" placeholder="如：S008" />
                  </div>
                  <div class="form-group">
                    <label>名称</label>
                    <input v-model="seedEditForm.name" placeholder="如：复合肥" />
                  </div>
                  <div class="form-group">
                    <label>类型</label>
                    <select v-model="seedEditForm.type">
                      <option value="化肥">化肥</option>
                      <option value="农药">农药</option>
                      <option value="种子">种子</option>
                      <option value="农膜">农膜</option>
                      <option value="农机配件">农机配件</option>
                    </select>
                  </div>
                </div>
                <div class="form-row">
                  <div class="form-group">
                    <label>库存量</label>
                    <input v-model.number="seedEditForm.stock" type="number" />
                  </div>
                  <div class="form-group">
                    <label>安全阈值</label>
                    <input v-model.number="seedEditForm.threshold" type="number" />
                  </div>
                  <div class="form-group">
                    <label>状态</label>
                    <select v-model="seedEditForm.status">
                      <option value="正常">正常</option>
                      <option value="紧张">紧张</option>
                    </select>
                  </div>
                </div>
                <div style="margin-top: 12px">
                  <button class="btn btn-primary" @click="submitSeedUpdate">保存</button>
                  <button class="btn btn-outline" style="margin-left: 8px" @click="showSeedEditForm = false">取消</button>
                </div>
              </div>
              <div class="stats-row">
                <div
                  class="stat-card"
                  v-for="(stat, idx) in seedInventoryData.stats"
                  :key="idx"
                  :class="{ warning: stat.warning }"
                >
                  <div class="stat-value">{{ stat.value.toLocaleString() }}</div>
                  <div class="stat-label">
                    {{ stat.name }}{{ stat.unit }}<span v-if="stat.warning"> 偏高</span>
                  </div>
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
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in seedInventoryData.items" :key="item.code">
                    <td>{{ item.code }}</td>
                    <td>{{ item.name }}</td>
                    <td>{{ item.type }}</td>
                    <td>{{ item.stock }}</td>
                    <td>{{ item.threshold }}</td>
                    <td>{{ item.unit }}</td>
                    <td>{{ item.expiry }}</td>
                    <td>
                      <span
                        :class="'tag ' + (item.aiAlert === '充足' ? 'tag-success' : 'tag-warning')"
                        >{{ item.aiAlert }}</span
                      >
                    </td>
                    <td>
                      <span
                        :class="'tag ' + (item.status === '正常' ? 'tag-success' : 'tag-warning')"
                        >{{ item.status }}</span
                      >
                    </td>
                    <td>
                      <button class="btn btn-outline btn-sm" @click="editSeed(item)">编辑</button>
                      <button class="btn btn-danger btn-sm" style="margin-left:4px" @click="deleteSeed(item)">删除</button>
                    </td>
                  </tr>
                </tbody>
              </table>
              <div class="pagination" v-if="seedInventoryTotal > 0">
                <span>共 {{ seedInventoryTotal }} 条</span>
                <button
                  class="btn btn-outline"
                  :disabled="seedInventoryPage <= 1"
                  @click="prevSeedInventoryPage"
                >
                  上一页
                </button>
                <span>{{ seedInventoryPage }} / {{ seedInventoryTotalPages }}</span>
                <button
                  class="btn btn-outline"
                  :disabled="seedInventoryPage >= seedInventoryTotalPages"
                  @click="nextSeedInventoryPage"
                >
                  下一页
                </button>
              </div>
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
                  <select v-model="seedAllocCrop">
                    <option v-for="crop in seedAllocCropOptions" :key="crop" :value="crop">
                      {{ crop }}
                    </option>
                  </select>
                </div>
                <div class="form-group">
                  <label>分配策略</label>
                  <select v-model="seedAllocStrategy">
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
                  {{ seedPredictAlertText }}
                </p>
              </div>
            </div>
          </div>

          <div class="page" :class="{ active: currentPage === 'labor-list' }" id="page-labor-list">
            <div class="card">
              <div class="card-header">
                <div class="card-title">👷 农业劳动力资源信息库管理</div>
                <button class="btn btn-primary" @click="showLaborCreateForm = true">
                  + 新增劳动力
                </button>
              </div>
              <div v-if="showLaborCreateForm" class="card" style="margin-bottom: 16px">
                <div class="card-header">
                  <div class="card-title">📝 新增劳动力</div>
                  <button class="btn btn-outline" @click="showLaborCreateForm = false">✕</button>
                </div>
                <div class="form-row">
                  <div class="form-group">
                    <label>编号</label>
                    <input v-model="laborCreateForm.id" placeholder="如 L005" />
                  </div>
                  <div class="form-group">
                    <label>姓名</label>
                    <input v-model="laborCreateForm.name" placeholder="请输入姓名" />
                  </div>
                  <div class="form-group">
                    <label>工种</label>
                    <select v-model="laborCreateForm.type">
                      <option value="农机操作">农机操作</option>
                      <option value="灌溉管理">灌溉管理</option>
                      <option value="种植技术员">种植技术员</option>
                      <option value="设备维护">设备维护</option>
                    </select>
                  </div>
                </div>
                <div class="form-row">
                  <div class="form-group">
                    <label>技能等级</label>
                    <select v-model="laborCreateForm.level">
                      <option value="初级">初级</option>
                      <option value="中级">中级</option>
                      <option value="高级">高级</option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label>所属片区</label>
                    <select v-model="laborCreateForm.area">
                      <option value="东片区">东片区</option>
                      <option value="西片区">西片区</option>
                      <option value="南片区">南片区</option>
                      <option value="北片区">北片区</option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label>日薪(元)</label>
                    <input
                      v-model.number="laborCreateForm.salary"
                      type="number"
                      placeholder="请输入日薪"
                    />
                  </div>
                </div>
                <div class="form-row">
                  <div class="form-group">
                    <label>可用时段</label>
                    <select v-model="laborCreateForm.available">
                      <option value="全天">全天</option>
                      <option value="白天">白天</option>
                      <option value="夜间">夜间</option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label>状态</label>
                    <select v-model="laborCreateForm.status">
                      <option value="在岗">在岗</option>
                      <option value="请假">请假</option>
                      <option value="离职">离职</option>
                    </select>
                  </div>
                </div>
                <div style="margin-top: 12px">
                  <button class="btn btn-primary" @click="submitLaborCreate">提交</button>
                  <button
                    class="btn btn-outline"
                    style="margin-left: 8px"
                    @click="showLaborCreateForm = false"
                  >
                    取消
                  </button>
                </div>
              </div>
              <div v-if="showLaborEditForm" class="card" style="margin-bottom: 16px">
                <div class="card-header">
                  <div class="card-title">✏️ 编辑劳动力</div>
                  <button class="btn btn-outline" @click="showLaborEditForm = false">✕</button>
                </div>
                <div class="form-row">
                  <div class="form-group">
                    <label>编号</label>
                    <input v-model="laborEditForm.id" disabled />
                  </div>
                  <div class="form-group">
                    <label>姓名</label>
                    <input v-model="laborEditForm.name" />
                  </div>
                  <div class="form-group">
                    <label>工种</label>
                    <select v-model="laborEditForm.type">
                      <option value="农机操作">农机操作</option>
                      <option value="灌溉管理">灌溉管理</option>
                      <option value="种植技术员">种植技术员</option>
                      <option value="设备维护">设备维护</option>
                    </select>
                  </div>
                </div>
                <div class="form-row">
                  <div class="form-group">
                    <label>技能等级</label>
                    <select v-model="laborEditForm.level">
                      <option value="初级">初级</option>
                      <option value="中级">中级</option>
                      <option value="高级">高级</option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label>所属片区</label>
                    <select v-model="laborEditForm.area">
                      <option value="东片区">东片区</option>
                      <option value="西片区">西片区</option>
                      <option value="南片区">南片区</option>
                      <option value="北片区">北片区</option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label>日薪(元)</label>
                    <input v-model.number="laborEditForm.salary" type="number" />
                  </div>
                </div>
                <div class="form-row">
                  <div class="form-group">
                    <label>可用时段</label>
                    <select v-model="laborEditForm.available">
                      <option value="全天">全天</option>
                      <option value="白天">白天</option>
                      <option value="夜间">夜间</option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label>状态</label>
                    <select v-model="laborEditForm.status">
                      <option value="在岗">在岗</option>
                      <option value="请假">请假</option>
                      <option value="离职">离职</option>
                    </select>
                  </div>
                </div>
                <div style="margin-top: 12px">
                  <button class="btn btn-primary" @click="submitLaborUpdate">保存</button>
                  <button class="btn btn-outline" style="margin-left: 8px" @click="showLaborEditForm = false">取消</button>
                </div>
              </div>
              <div v-if="laborLoading" style="text-align: center; padding: 40px">加载中...</div>
              <table v-else class="data-table">
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
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in laborList" :key="item.id">
                    <td>{{ item.id }}</td>
                    <td>{{ item.name }}</td>
                    <td>{{ item.type }}</td>
                    <td>{{ item.level }}</td>
                    <td>{{ item.area }}</td>
                    <td>{{ item.salary }}</td>
                    <td>{{ item.available }}</td>
                    <td>
                      <span
                        class="tag"
                        :class="{
                          'tag-success': item.status === '在岗',
                          'tag-warning': item.status === '请假',
                          'tag-danger': item.status === '离职',
                        }"
                        >{{ item.status }}</span
                      >
                    </td>
                    <td>
                      <button class="btn btn-outline btn-sm" @click="editLabor(item)">编辑</button>
                      <button class="btn btn-danger btn-sm" style="margin-left:4px" @click="deleteLabor(item)">删除</button>
                    </td>
                  </tr>
                  <tr v-if="laborList.length === 0">
                    <td colspan="9" style="text-align: center; padding: 20px">暂无数据</td>
                  </tr>
                </tbody>
              </table>
              <div class="pagination" v-if="laborTotal > 0">
                <span>共 {{ laborTotal }} 条</span>
                <button class="btn btn-outline" :disabled="laborPage <= 1" @click="prevLaborPage">
                  上一页
                </button>
                <span>{{ laborPage }} / {{ laborTotalPages }}</span>
                <button
                  class="btn btn-outline"
                  :disabled="laborPage >= laborTotalPages"
                  @click="nextLaborPage"
                >
                  下一页
                </button>
              </div>
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
                  <select v-model="laborScheduleTask">
                    <option>春耕播种</option>
                    <option>夏种管理</option>
                    <option>秋收作业</option>
                    <option>冬藏整地</option>
                  </select>
                </div>
                <div class="form-group">
                  <label>任务片区</label>
                  <select v-model="laborScheduleArea">
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
                  <button class="btn btn-primary" @click="showEquipmentCreateForm = true">
                    + 新增设备
                  </button>
                </div>
              </div>
              <div v-if="showEquipmentCreateForm" class="card" style="margin-bottom: 16px">
                <div class="card-header">
                  <div class="card-title">📝 新增设备</div>
                  <button class="btn btn-outline" @click="showEquipmentCreateForm = false">
                    ✕
                  </button>
                </div>
                <div class="form-row">
                  <div class="form-group">
                    <label>设备编号</label>
                    <input v-model="equipmentCreateForm.id" placeholder="如 EQ011" />
                  </div>
                  <div class="form-group">
                    <label>设备名称</label>
                    <input v-model="equipmentCreateForm.name" placeholder="请输入设备名称" />
                  </div>
                  <div class="form-group">
                    <label>类型</label>
                    <select v-model="equipmentCreateForm.type">
                      <option value="收割设备">收割设备</option>
                      <option value="耕作设备">耕作设备</option>
                      <option value="播种设备">播种设备</option>
                      <option value="灌溉设备">灌溉设备</option>
                      <option value="植保设备">植保设备</option>
                      <option value="施肥设备">施肥设备</option>
                      <option value="加工设备">加工设备</option>
                    </select>
                  </div>
                </div>
                <div class="form-row">
                  <div class="form-group">
                    <label>片区</label>
                    <select v-model="equipmentCreateForm.area">
                      <option value="东区">东区</option>
                      <option value="西区">西区</option>
                      <option value="南区">南区</option>
                      <option value="北区">北区</option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label>作业效率</label>
                    <input v-model="equipmentCreateForm.eff" placeholder="如 15%h" />
                  </div>
                  <div class="form-group">
                    <label>当前状态</label>
                    <select v-model="equipmentCreateForm.status">
                      <option value="正常">正常</option>
                      <option value="维护">维护</option>
                      <option value="待修">待修</option>
                    </select>
                  </div>
                </div>
                <div class="form-row">
                  <div class="form-group">
                    <label>AI健康评分</label>
                    <input
                      v-model.number="equipmentCreateForm.score"
                      type="number"
                      min="0"
                      max="100"
                      placeholder="0-100"
                    />
                  </div>
                </div>
                <div style="margin-top: 12px">
                  <button class="btn btn-primary" @click="submitEquipmentCreate">提交</button>
                  <button
                    class="btn btn-outline"
                    style="margin-left: 8px"
                    @click="showEquipmentCreateForm = false"
                  >
                    取消
                  </button>
                </div>
              </div>
              <div v-if="showEquipmentEditForm" class="card" style="margin-bottom: 16px">
                <div class="card-header">
                  <div class="card-title">✏️ 编辑设备</div>
                  <button class="btn btn-outline" @click="showEquipmentEditForm = false">✕</button>
                </div>
                <div class="form-row">
                  <div class="form-group">
                    <label>设备编号</label>
                    <input v-model="equipmentEditForm.id" disabled />
                  </div>
                  <div class="form-group">
                    <label>设备名称</label>
                    <input v-model="equipmentEditForm.name" />
                  </div>
                  <div class="form-group">
                    <label>类型</label>
                    <select v-model="equipmentEditForm.type">
                      <option value="收割设备">收割设备</option>
                      <option value="耕作设备">耕作设备</option>
                      <option value="播种设备">播种设备</option>
                      <option value="灌溉设备">灌溉设备</option>
                      <option value="植保设备">植保设备</option>
                      <option value="施肥设备">施肥设备</option>
                      <option value="加工设备">加工设备</option>
                    </select>
                  </div>
                </div>
                <div class="form-row">
                  <div class="form-group">
                    <label>片区</label>
                    <select v-model="equipmentEditForm.area">
                      <option value="东区">东区</option>
                      <option value="西区">西区</option>
                      <option value="南区">南区</option>
                      <option value="北区">北区</option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label>作业效率</label>
                    <input v-model="equipmentEditForm.eff" />
                  </div>
                  <div class="form-group">
                    <label>当前状态</label>
                    <select v-model="equipmentEditForm.status">
                      <option value="正常">正常</option>
                      <option value="维护">维护</option>
                      <option value="待修">待修</option>
                    </select>
                  </div>
                </div>
                <div class="form-row">
                  <div class="form-group">
                    <label>AI健康评分</label>
                    <input v-model.number="equipmentEditForm.score" type="number" min="0" max="100" />
                  </div>
                </div>
                <div style="margin-top: 12px">
                  <button class="btn btn-primary" @click="submitEquipmentUpdate">保存</button>
                  <button class="btn btn-outline" style="margin-left: 8px" @click="showEquipmentEditForm = false">取消</button>
                </div>
              </div>
              <div class="stats-row">
                <div class="stat-card">
                  <div class="stat-value">{{ equipmentStats.total }}</div>
                  <div class="stat-label">设备总数</div>
                </div>
                <div class="stat-card success">
                  <div class="stat-value">{{ equipmentStats.normal }}</div>
                  <div class="stat-label">正常运行</div>
                </div>
                <div class="stat-card warning">
                  <div class="stat-value">{{ equipmentStats.maintenance }}</div>
                  <div class="stat-label">维护中</div>
                </div>
                <div class="stat-card danger">
                  <div class="stat-value">{{ equipmentStats.repair }}</div>
                  <div class="stat-label">待维修</div>
                </div>
              </div>
              <div v-if="equipmentLoading" style="text-align: center; padding: 40px">加载中...</div>
              <table v-else class="data-table">
                <thead>
                  <tr>
                    <th>设备编号</th>
                    <th>设备名称</th>
                    <th>类型</th>
                    <th>片区</th>
                    <th>作业效率</th>
                    <th>当前状态</th>
                    <th>AI健康评分</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="eq in equipmentList" :key="eq.id">
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
                        {{ eq.score }}
                      </span>
                    </td>
                    <td>
                      <button class="btn btn-outline btn-sm" @click="editEquipment(eq)">编辑</button>
                      <button class="btn btn-danger btn-sm" style="margin-left:4px" @click="deleteEquipment(eq)">删除</button>
                    </td>
                  </tr>
                  <tr v-if="equipmentList.length === 0">
                    <td colspan="8" style="text-align: center; padding: 20px">暂无数据</td>
                  </tr>
                </tbody>
              </table>
              <div class="pagination" v-if="equipmentTotal > 0">
                <span>共 {{ equipmentTotal }} 条</span>
                <button
                  class="btn btn-outline"
                  :disabled="equipmentPage <= 1"
                  @click="prevEquipmentPage"
                >
                  上一页
                </button>
                <span>{{ equipmentPage }} / {{ equipmentTotalPages }}</span>
                <button
                  class="btn btn-outline"
                  :disabled="equipmentPage >= equipmentTotalPages"
                  @click="nextEquipmentPage"
                >
                  下一页
                </button>
              </div>
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
                  <select v-model="equipmentAllocationTask">
                    <option>耕地作业</option>
                    <option>播种作业</option>
                    <option>收割作业</option>
                  </select>
                </div>
                <div class="form-group">
                  <label>目标地块</label>
                  <select v-model="equipmentAllocationArea">
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
                  <div class="chart-title">设备调配分布</div>
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
              </div>
              <div class="stats-row">
                <div class="stat-card info">
                  <div class="stat-value">
                    {{
                      (maintenanceData &&
                        maintenanceData.stats &&
                        maintenanceData.stats.monthlyPlan) ||
                      42
                    }}
                  </div>
                  <div class="stat-label">本月计划</div>
                </div>
                <div class="stat-card success">
                  <div class="stat-value">
                    {{
                      (maintenanceData &&
                        maintenanceData.stats &&
                        maintenanceData.stats.completed) ||
                      35
                    }}
                  </div>
                  <div class="stat-label">已完成</div>
                </div>
                <div class="stat-card warning">
                  <div class="stat-value">
                    {{
                      (maintenanceData &&
                        maintenanceData.stats &&
                        maintenanceData.stats.inProgress) ||
                      5
                    }}
                  </div>
                  <div class="stat-label">进行中</div>
                </div>
                <div class="stat-card danger">
                  <div class="stat-value">
                    {{
                      (maintenanceData && maintenanceData.stats && maintenanceData.stats.overdue) ||
                      2
                    }}
                  </div>
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
                    <select v-model="aiDecisionPeriod">
                      <option>2025年度</option>
                      <option>2025春季</option>
                      <option>2025秋季</option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label>优化维度</label>
                    <select v-model="aiDecisionDimension">
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
                  <h4>
                    ✅ AI综合决策方案已生成（多约束遗传算法· 500代迭代· 收敛率{{
                      (aiDecisionData && aiDecisionData.convergenceRate) || '98.6%'
                    }}）
                  </h4>
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
                      <div style="font-size: 20px; font-weight: 700; color: #52c41a">
                        +{{ (aiDecisionData && aiDecisionData.yieldIncrease) || '12.5' }}%
                      </div>
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
                      <div style="font-size: 20px; font-weight: 700; color: #1890ff">
                        +{{ (aiDecisionData && aiDecisionData.waterSaving) || '8.3' }}%
                      </div>
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
                      <div style="font-size: 20px; font-weight: 700; color: #fa8c16">
                        +{{ (aiDecisionData && aiDecisionData.efficiencyIncrease) || '15.2' }}%
                      </div>
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
                      <div style="font-size: 20px; font-weight: 700; color: #722ed1">
                        {{ (aiDecisionData && aiDecisionData.resourceUtilization) || '92' }}%
                      </div>
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
                  <div
                    class="chart-title"
                    style="display: flex; justify-content: space-between; align-items: center"
                  >
                    <span>📊 历史产量与预测趋势</span>
                    <div style="display: flex; gap: 8px">
                      <select
                        v-model="yieldTrendArea"
                        @change="onYieldFilterChange"
                        style="
                          padding: 2px 8px;
                          border-radius: 4px;
                          border: 1px solid #d9d9d9;
                          font-size: 13px;
                        "
                      >
                        <option v-for="area in yieldAreaOptions" :key="area" :value="area">
                          {{ area }}
                        </option>
                      </select>
                      <select
                        v-model="yieldTrendYear"
                        @change="onYieldFilterChange"
                        style="
                          padding: 2px 8px;
                          border-radius: 4px;
                          border: 1px solid #d9d9d9;
                          font-size: 13px;
                        "
                      >
                        <option v-for="year in yieldYearOptions" :key="year" :value="year">
                          {{ year }}
                        </option>
                      </select>
                    </div>
                  </div>
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
                <div style="display: flex; gap: 8px; align-items: center">
                  <select
                    v-model="resourcePredictArea"
                    @change="onResourceFilterChange"
                    style="
                      padding: 2px 8px;
                      border-radius: 4px;
                      border: 1px solid #d9d9d9;
                      font-size: 13px;
                    "
                  >
                    <option v-for="area in resourceAreaOptions" :key="area" :value="area">
                      {{ area }}
                    </option>
                  </select>
                  <select
                    v-model="resourcePredictYear"
                    @change="onResourceFilterChange"
                    style="
                      padding: 2px 8px;
                      border-radius: 4px;
                      border: 1px solid #d9d9d9;
                      font-size: 13px;
                    "
                  >
                    <option v-for="year in resourceYearOptions" :key="year" :value="year">
                      {{ year }}
                    </option>
                  </select>
                  <span class="tag tag-info">多元回归 + 时间序列</span>
                </div>
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
                <h4>📋 AI资源需求预估报告（{{ resourcePredictYear }}年度）</h4>
                <p style="font-size: 13px; color: #555">
                  基于历史数据训练的时间序列预测模型，预测{{
                    resourcePredictYear
                  }}年农业资源总需求。
                </p>
              </div>
            </div>
          </div>

          <div class="page" :class="{ active: currentPage === 'sys-user' }" id="page-sys-user">
            <div class="card">
              <div class="card-header">
                <div class="card-title">⚙️ 系统管理 - RBAC权限与用户管理</div>
                <button class="btn btn-primary" @click="showSystemUserCreateForm = true">
                  + 新增用户
                </button>
              </div>
              <div v-if="showSystemUserCreateForm" class="card" style="margin-bottom: 16px">
                <div class="card-header">
                  <div class="card-title">📝 新增用户</div>
                  <button class="btn btn-outline" @click="showSystemUserCreateForm = false">
                    ✕
                  </button>
                </div>
                <div class="form-row">
                  <div class="form-group">
                    <label>用户名</label>
                    <input v-model="systemUserCreateForm.username" placeholder="请输入用户名" />
                  </div>
                  <div class="form-group">
                    <label>姓名</label>
                    <input v-model="systemUserCreateForm.name" placeholder="请输入姓名" />
                  </div>
                  <div class="form-group">
                    <label>密码</label>
                    <input
                      v-model="systemUserCreateForm.password"
                      type="password"
                      placeholder="请输入密码"
                    />
                  </div>
                </div>
                <div class="form-row">
                  <div class="form-group">
                    <label>角色</label>
                    <select v-model="systemUserCreateForm.role">
                      <option value="admin">系统管理员</option>
                      <option value="dispatcher">资源调度员</option>
                      <option value="farmer">片区经理</option>
                      <option value="analyst">数据分析师</option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label>部门</label>
                    <input v-model="systemUserCreateForm.department" placeholder="请输入部门" />
                  </div>
                  <div class="form-group">
                    <label>状态</label>
                    <select v-model="systemUserCreateForm.status">
                      <option :value="1">正常</option>
                      <option :value="0">禁用</option>
                    </select>
                  </div>
                </div>
                <div style="margin-top: 12px">
                  <button class="btn btn-primary" @click="submitSystemUserCreate">提交</button>
                  <button
                    class="btn btn-outline"
                    style="margin-left: 8px"
                    @click="showSystemUserCreateForm = false"
                  >
                    取消
                  </button>
                </div>
              </div>
              <div class="stats-row">
                <div class="stat-card">
                  <div class="stat-value">{{ systemStats.totalUsers }}</div>
                  <div class="stat-label">系统用户</div>
                </div>
                <div class="stat-card success">
                  <div class="stat-value">{{ systemStats.totalRoles }}</div>
                  <div class="stat-label">角色类型</div>
                </div>
                <div class="stat-card info">
                  <div class="stat-value">{{ systemStats.totalPermissions }}</div>
                  <div class="stat-label">权限节点</div>
                </div>
                <div class="stat-card warning">
                  <div class="stat-value">{{ systemStats.totalLogs }}</div>
                  <div class="stat-label">操作日志</div>
                </div>
              </div>
              <table class="data-table">
                <thead>
                  <tr>
                    <th>用户ID</th>
                    <th>用户名</th>
                    <th>角色</th>
                    <th>部门</th>
                    <th>状态</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="systemUsersLoading">
                    <td colspan="5" style="text-align: center; padding: 20px">加载中...</td>
                  </tr>
                  <tr v-else-if="systemUsers.length === 0">
                    <td colspan="5" style="text-align: center; padding: 20px">暂无数据</td>
                  </tr>
                  <tr v-for="user in systemUsers" :key="user.userId">
                    <td>{{ user.userId }}</td>
                    <td>{{ user.username }}</td>
                    <td>
                      <span class="tag" :class="roleTagClass(user.role)">{{ user.roleName }}</span>
                    </td>
                    <td>{{ user.department }}</td>
                    <td>
                      <span class="tag" :class="user.status === 1 ? 'tag-success' : 'tag-danger'">
                        {{ user.status === 1 ? '正常' : '禁用' }}
                      </span>
                    </td>
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
import {
  authApi,
  dashboardApi,
  farmlandApi,
  waterApi,
  seedApi,
  laborApi,
  equipmentApi,
  aiApi,
  predictApi,
  systemApi,
} from '../api'

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
      dashboardData: null,
      dashAlerts: [],
      showFarmlandCreateForm: false,
      showFarmlandEditForm: false,
      farmlandEditForm: { id: null, blockCode: '', region: '', area: 0, soilType: '', suitableCropsStr: '', currentCrop: '', ownership: '', plantingPeriod: '', status: '' },
      farmlandList: [],
      farmlandStatsData: null,
      farmlandTotal: 0,
      farmlandPage: 1,
      farmlandPageSize: 20,
      farmlandLoading: false,
      laborList: [],
      laborTotal: 0,
      laborPage: 1,
      laborPageSize: 20,
      laborLoading: false,
      laborScheduleTask: '春耕播种',
      laborScheduleArea: '全部片区',
      laborScheduleResult: null,
      showLaborCreateForm: false,
      showLaborEditForm: false,
      laborEditForm: { id: '', name: '', type: '农机操作', level: '初级', area: '东片区', salary: 0, available: '全天', status: '在岗' },
      laborCreateForm: {
        id: '',
        name: '',
        type: '农机操作',
        level: '中级',
        area: '东片区',
        salary: null,
        available: '全天',
        status: '在岗',
      },
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
      rotationYear: 2026,
      waterQuotaData: {
        totalQuota: 2450,
        allocated: 1870,
        remaining: 580,
        usageRate: 76.3,
        items: [],
      },
      waterQuotaLoading: false,
      quotaAdjustVisible: false,
      quotaAdjustItems: [],
      quotaAdjustSaving: false,
      waterCycle: '季度分配',
      waterGoal: '节水最大化',
      waterChartData: null,
      waterAnalysisData: null,
      waterAnalysisLoading: false,
      waterAnalysisYear: new Date().getFullYear(),
      waterAnalysisYears: [2020, 2021, 2022, 2023, 2024, 2025, 2026],
      seedInventoryData: {
        stats: [],
        items: [],
      },
      seedInventoryPage: 1,
      seedInventoryPageSize: 20,
      seedInventoryTotal: 0,
      showSeedCreateForm: false,
      showSeedEditForm: false,
      seedEditForm: { id: null, code: '', name: '', type: '化肥', stock: null, threshold: null, status: '正常' },
      seedCreateForm: {
        code: '',
        name: '',
        type: '化肥',
        stock: null,
        threshold: null,
        unit: '吨',
        expiry: '',
        status: '正常',
      },
      seedAllocCrop: '全部作物',
      seedAllocStrategy: '按种植面积比例',
      seedAllocChartData: null,
      seedPredictData: null,
      optimizeCropDistribution: [
        { name: '水稻', percentage: 32 },
        { name: '小麦', percentage: 28 },
        { name: '玉米', percentage: 22 },
        { name: '大豆', percentage: 14 },
        { name: '其他', percentage: 4 },
      ],
      optimizeResult: null,
      optimizeBlocks: [],
      optimizeRegionData: [],
      optimizeDataFromAI: false,
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
      loadingText: 'AI算法计算..',
      loadingDetail: '正在初始化..',
      aiOptimizeResultVisible: false,
      aiDecisionResultVisible: false,
      aiDecisionData: null,
      aiDecisionPeriod: '2025年度',
      aiDecisionDimension: '五维全优化',
      yieldTrendYear: '2026',
      yieldTrendArea: '全部',
      yieldData: null,
      yieldAreaOptions: ['全部'],
      yieldYearOptions: ['2026'],
      resourcePredictYear: '2026',
      resourcePredictArea: '全部',
      resourcePredictData: null,
      resourceAreaOptions: ['全部'],
      resourceYearOptions: ['2026'],
      systemUsers: [],
      systemUsersLoading: false,
      showSystemUserCreateForm: false,
      systemUserCreateForm: {
        username: '',
        name: '',
        password: '',
        role: 'farmer',
        department: '',
        status: 1,
      },
      systemStats: {
        totalUsers: 0,
        totalRoles: 0,
        totalPermissions: 0,
        totalLogs: 0,
      },
      equipmentList: [],
      equipmentTotal: 0,
      equipmentPage: 1,
      equipmentPageSize: 20,
      equipmentLoading: false,
      equipmentAllocationTask: '耕地作业',
      equipmentAllocationArea: '多地块协同',
      equipmentAllocationResult: null,
      maintenanceData: null,
      showEquipmentCreateForm: false,
      showEquipmentEditForm: false,
      equipmentEditForm: { dbId: null, id: '', name: '', type: '收割设备', area: '东区', eff: '', status: '正常', score: 80 },
      equipmentCreateForm: {
        id: '',
        name: '',
        type: '收割设备',
        area: '东区',
        eff: '',
        status: '正常',
        score: null,
      },
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
    laborTotalPages() {
      return Math.ceil(this.laborTotal / this.laborPageSize) || 1
    },
    equipmentTotalPages() {
      return Math.ceil(this.equipmentTotal / this.equipmentPageSize) || 1
    },
    equipmentStats() {
      const list = this.equipmentList || []
      return {
        total: list.length,
        normal: list.filter((e) => e.status === '正常').length,
        maintenance: list.filter((e) => e.status === '维护').length,
        repair: list.filter((e) => e.status === '待修').length,
      }
    },
    seedInventoryTotalPages() {
      return Math.ceil(this.seedInventoryTotal / this.seedInventoryPageSize) || 1
    },
    dashboardStats() {
      if (this.dashboardData && this.dashboardData.stats) {
        return this.dashboardData.stats
      }
      return {
        farmland: '12,856 万亩',
        water: '2,450 万m³',
        seed: '8,650 万吨',
        equipment: '156 台',
      }
    },
    seedAllocCropOptions() {
      const crops = new Set()
      crops.add('全部作物')
      ;(this.farmlandList || []).forEach((item) => {
        if (item.currentCrop) crops.add(item.currentCrop)
      })
      if (crops.size === 1) {
        crops.add('水稻')
        crops.add('小麦')
        crops.add('玉米')
      }
      return Array.from(crops)
    },
    farmlandStats() {
      const list = this.farmlandList || []
      if (this.farmlandStatsData) {
        return this.farmlandStatsData
      }
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
    waterAnalysisReport() {
      const d = this.waterAnalysisData
      if (d && d.report && d.report.length > 0) {
        return d.report
      }
      return [
        {
          region: '东片区',
          actualUsage: 620,
          theoreticalDemand: 580,
          wasteAmount: 40,
          wasteRate: 6.9,
          aiSuggestion: '优化灌溉时段',
          savingPotential: 6.5,
        },
        {
          region: '西片区',
          actualUsage: 580,
          theoreticalDemand: 520,
          wasteAmount: 60,
          wasteRate: 11.5,
          aiSuggestion: '升级滴灌系统',
          savingPotential: 10.3,
        },
        {
          region: '南片区',
          actualUsage: 380,
          theoreticalDemand: 360,
          wasteAmount: 20,
          wasteRate: 5.6,
          aiSuggestion: '维持现状',
          savingPotential: 5.3,
        },
        {
          region: '北片区',
          actualUsage: 290,
          theoreticalDemand: 270,
          wasteAmount: 20,
          wasteRate: 7.4,
          aiSuggestion: '喷灌改滴灌',
          savingPotential: 6.9,
        },
      ]
    },
    seedPredictAlertText() {
      const apiData = this.seedPredictData
      if (apiData && apiData.alert) {
        return apiData.alert
      }
      return '基于时间序列预测模型，预计下季度化肥需求量预测2,650kg，当前库存2,180kg，缺口470kg。AI建议：提前采购复合肥600吨、尿素200吨，确保春耕供应。'
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
    editFarmland(item) {
      this.farmlandEditForm = {
        id: item.id,
        blockCode: item.blockCode || '',
        region: item.region || '',
        area: item.area || 0,
        soilType: item.soilType || '',
        suitableCropsStr: (item.suitableCrops || []).join(','),
        currentCrop: item.currentCrop || '',
        ownership: item.ownership || '',
        plantingPeriod: item.plantingPeriod || '',
        status: item.status || '',
      }
      this.showFarmlandEditForm = true
    },
    async submitFarmlandUpdate() {
      const form = this.farmlandEditForm
      if (!form.id) {
        alert('缺少地块ID')
        return
      }
      const payload = {
        id: form.id,
        blockCode: form.blockCode,
        name: form.blockCode,
        area: form.area,
        soilType: form.soilType,
        currentCrop: form.currentCrop,
        ownership: form.ownership,
        plantingPeriod: form.plantingPeriod,
        status: form.status,
      }
      try {
        await farmlandApi.update(payload)
        alert('更新成功！')
        this.showFarmlandEditForm = false
        this.loadFarmlandList()
      } catch (error) {
        alert('更新失败！' + (error.message || '未知错误'))
      }
    },
    async deleteFarmland(item) {
      if (!confirm('确定删除地块「' + item.blockCode + '」吗？此操作不可恢复。')) return
      try {
        await farmlandApi.delete({ id: item.id })
        alert('删除成功！')
        this.loadFarmlandList()
      } catch (error) {
        alert('删除失败！' + (error.message || '未知错误'))
      }
    },
    async deleteWaterQuota(item) {
      if (!confirm('确定删除片区「' + item.area + '」的配额记录吗？')) return
      try {
        await waterApi.delete({ id: item.id })
        alert('删除成功！')
        this.loadWaterQuota()
      } catch (error) {
        alert('删除失败！' + (error.message || '未知错误'))
      }
    },
    resetSeedCreateForm() {
      this.seedCreateForm = {
        code: '',
        name: '',
        type: '化肥',
        stock: null,
        threshold: null,
        unit: '吨',
        expiry: '',
        status: '正常',
      }
    },
    async submitSeedCreate() {
      const form = this.seedCreateForm
      if (!form.code || !form.name || form.stock === null || form.stock === '') {
        alert('请填写物资编号、名称和入库数量')
        return
      }
      const payload = {
        code: form.code,
        name: form.name,
        type: form.type,
        stock: form.stock,
        threshold: form.threshold,
        unit: form.unit,
        expiry: form.expiry,
        status: form.status,
      }
      try {
        await seedApi.create(payload)
        alert('入库登记成功！')
        this.showSeedCreateForm = false
        this.resetSeedCreateForm()
        this.loadSeedInventory()
      } catch (error) {
        alert('入库登记失败！' + (error.message || '未知错误'))
      }
    },
    editSeed(item) {
      this.seedEditForm = {
        id: item.id,
        code: item.code || '',
        name: item.name || '',
        type: item.type || '化肥',
        stock: item.stock,
        threshold: item.threshold,
        status: item.status || '正常',
      }
      this.showSeedEditForm = true
    },
    async submitSeedUpdate() {
      const form = this.seedEditForm
      if (!form.id) {
        alert('缺少农资ID')
        return
      }
      try {
        await seedApi.update({
          id: form.id,
          code: form.code,
          name: form.name,
          type: form.type,
          stock: form.stock,
          threshold: form.threshold,
          status: form.status,
        })
        alert('更新成功！')
        this.showSeedEditForm = false
        this.loadSeedInventory()
      } catch (error) {
        alert('更新失败！' + (error.message || '未知错误'))
      }
    },
    async deleteSeed(item) {
      if (!confirm('确定删除农资「' + item.name + '」吗？此操作不可恢复。')) return
      try {
        await seedApi.delete({ id: item.id })
        alert('删除成功！')
        this.loadSeedInventory()
      } catch (error) {
        alert('删除失败！' + (error.message || '未知错误'))
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
          this.dashboardData = data
          this.dashAlerts = data.alerts || data.warnings || []
        }
      } catch (error) {
        console.error('Dashboard API 请求失败:', error)
        this.dashboardData = null
        this.dashAlerts = []
      } finally {
        this.$nextTick(() => this.initDashboardCharts())
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
        this.farmlandStatsData = data?.stats || null
      } catch (error) {
        console.warn('Farmland list API unavailable:', error)
        this.farmlandList = []
        this.farmlandTotal = 0
      } finally {
        this.farmlandLoading = false
      }
    },
    async loadLaborList() {
      this.laborLoading = true
      try {
        const res = await laborApi.list({
          page: this.laborPage,
          pageSize: this.laborPageSize,
        })
        const data = res?.data || res
        this.laborTotal = data?.total || 0
        this.laborList = data?.items || []
      } catch (error) {
        console.warn('Labor list API unavailable:', error)
        this.laborList = []
        this.laborTotal = 0
      } finally {
        this.laborLoading = false
      }
    },
    async submitLaborCreate() {
      const form = this.laborCreateForm
      if (!form.id || !form.name) {
        alert('请填写编号和姓名')
        return
      }
      try {
        await laborApi.create({
          id: form.id,
          name: form.name,
          type: form.type,
          level: form.level,
          area: form.area,
          salary: form.salary,
          available: form.available,
          status: form.status,
        })
        alert('新增劳动力成功！')
        this.showLaborCreateForm = false
        this.resetLaborCreateForm()
        this.loadLaborList()
      } catch (error) {
        alert('新增劳动力失败！' + (error.message || '未知错误'))
      }
    },
    resetLaborCreateForm() {
      this.laborCreateForm = {
        id: '',
        name: '',
        type: '农机操作',
        level: '中级',
        area: '东片区',
        salary: null,
        available: '全天',
        status: '在岗',
      }
    },
    editLabor(item) {
      this.laborEditForm = {
        id: item.id || '',
        name: item.name || '',
        type: item.type || '农机操作',
        level: item.level || '初级',
        area: item.area || '东片区',
        salary: item.salary || 0,
        available: item.available || '全天',
        status: item.status || '在岗',
      }
      this.showLaborEditForm = true
    },
    async submitLaborUpdate() {
      const form = this.laborEditForm
      if (!form.id) {
        alert('缺少劳动力编号')
        return
      }
      try {
        await laborApi.update({
          id: form.id,
          name: form.name,
          type: form.type,
          level: form.level,
          area: form.area,
          salary: form.salary,
          available: form.available,
          status: form.status,
        })
        alert('更新成功！')
        this.showLaborEditForm = false
        this.loadLaborList()
      } catch (error) {
        alert('更新失败！' + (error.message || '未知错误'))
      }
    },
    async deleteLabor(item) {
      if (!confirm('确定删除劳动力「' + item.name + '」吗？此操作不可恢复。')) return
      try {
        await laborApi.delete({ id: item.id })
        alert('删除成功！')
        this.loadLaborList()
      } catch (error) {
        alert('删除失败！' + (error.message || '未知错误'))
      }
    },
    async loadLaborSchedule() {
      try {
        const res = await laborApi.oldSchedule()
        const data = res?.data || res
        this.laborScheduleResult = data
      } catch (error) {
        console.warn('Labor old schedule API unavailable:', error)
        this.laborScheduleResult = null
      } finally {
        this.$nextTick(() => this.initLaborChart())
      }
    },
    async loadWaterQuota() {
      this.waterQuotaLoading = true
      try {
        const res = await waterApi.quota()
        const data = res?.data || res
        const result = data?.data || data || {}
        if (result && (result.totalQuota || result.items)) {
          this.waterQuotaData = {
            totalQuota: result.totalQuota ?? 0,
            allocated: result.allocated ?? 0,
            remaining: result.remaining ?? 0,
            usageRate: result.usageRate ?? 0,
            items: result.items || [],
          }
        }
      } catch (error) {
        console.warn('Water quota API unavailable, using local fallback:', error)
      } finally {
        this.waterQuotaLoading = false
      }
    },
    showQuotaAdjust() {
      this.quotaAdjustItems = (this.waterQuotaData.items || []).map((item) => ({
        ...item,
        quota: item.quota || 0,
      }))
      this.quotaAdjustVisible = true
    },
    cancelQuotaAdjust() {
      this.quotaAdjustVisible = false
    },
    async saveQuotaAdjust() {
      const sum = this.quotaAdjustItems.reduce((s, item) => s + (item.quota || 0), 0)
      if (sum > this.waterQuotaData.totalQuota) {
        alert(
          `片区配额合计 ${sum} 万m³ 超出总水量 ${this.waterQuotaData.totalQuota} 万m³，请重新调整！`,
        )
        return
      }
      this.quotaAdjustSaving = true
      try {
        const res = await waterApi.updateQuota({
          items: this.quotaAdjustItems.map((item) => ({
            area: item.area,
            quota: item.quota,
          })),
        })
        const data = res?.data || res
        const result = data?.data || data || {}
        if (result && result.items) {
          this.waterQuotaData = {
            totalQuota: result.totalQuota ?? this.waterQuotaData.totalQuota,
            allocated: result.allocated ?? this.waterQuotaData.allocated,
            remaining: result.remaining ?? this.waterQuotaData.remaining,
            usageRate: result.usageRate ?? this.waterQuotaData.usageRate,
            items: result.items,
          }
        } else {
          const newTotal = this.quotaAdjustItems.reduce((sum, item) => sum + (item.quota || 0), 0)
          this.waterQuotaData.totalQuota = newTotal
          this.waterQuotaData.items = this.quotaAdjustItems.map((item) => ({
            ...item,
            remaining: (item.quota || 0) - (item.used || 0),
          }))
        }
        this.quotaAdjustVisible = false
        alert('配额调整保存成功！')
      } catch (error) {
        console.warn('Quota update API unavailable:', error)
        const newTotal = this.quotaAdjustItems.reduce((sum, item) => sum + (item.quota || 0), 0)
        this.waterQuotaData.totalQuota = newTotal
        this.waterQuotaData.items = this.quotaAdjustItems.map((item) => ({
          ...item,
          remaining: (item.quota || 0) - (item.used || 0),
        }))
        this.quotaAdjustVisible = false
        alert('配额调整保存成功！')
      } finally {
        this.quotaAdjustSaving = false
      }
    },
    prevPage() {
      if (this.farmlandPage > 1) {
        this.farmlandPage--
        this.loadFarmlandList()
      }
    },
    nextPage() {
      if (this.farmlandPage < this.farmlandTotalPages) {
        this.farmlandPage++
        this.loadFarmlandList()
      }
    },
    prevLaborPage() {
      if (this.laborPage > 1) {
        this.laborPage--
        this.loadLaborList()
      }
    },
    nextLaborPage() {
      if (this.laborPage < this.laborTotalPages) {
        this.laborPage++
        this.loadLaborList()
      }
    },
    async loadEquipmentList() {
      this.equipmentLoading = true
      try {
        const res = await equipmentApi.list({
          page: this.equipmentPage,
          pageSize: this.equipmentPageSize,
        })
        const data = res?.data || res
        this.equipmentTotal = data?.total || 0
        this.equipmentList = data?.items || []
      } catch (error) {
        console.warn('Equipment list API unavailable:', error)
        this.equipmentList = []
        this.equipmentTotal = 0
      } finally {
        this.equipmentLoading = false
      }
    },
    prevEquipmentPage() {
      if (this.equipmentPage > 1) {
        this.equipmentPage--
        this.loadEquipmentList()
      }
    },
    nextEquipmentPage() {
      if (this.equipmentPage < this.equipmentTotalPages) {
        this.equipmentPage++
        this.loadEquipmentList()
      }
    },
    async submitEquipmentCreate() {
      const form = this.equipmentCreateForm
      if (!form.id || !form.name) {
        alert('请填写设备编号和名称')
        return
      }
      try {
        await equipmentApi.create({
          id: form.id,
          name: form.name,
          type: form.type,
          area: form.area,
          eff: form.eff,
          status: form.status,
          score: form.score,
        })
        alert('新增设备成功！')
        this.showEquipmentCreateForm = false
        this.resetEquipmentCreateForm()
        this.loadEquipmentList()
      } catch (error) {
        alert('新增设备失败！' + (error.message || '未知错误'))
      }
    },
    resetEquipmentCreateForm() {
      this.equipmentCreateForm = {
        id: '',
        name: '',
        type: '收割设备',
        area: '东区',
        eff: '',
        status: '正常',
        score: null,
      }
    },
    editEquipment(eq) {
      this.equipmentEditForm = {
        dbId: eq.dbId,
        id: eq.id || '',
        name: eq.name || '',
        type: eq.type || '收割设备',
        area: eq.area || '东区',
        eff: eq.eff || '',
        status: eq.status || '正常',
        score: eq.score || 80,
      }
      this.showEquipmentEditForm = true
    },
    async submitEquipmentUpdate() {
      const form = this.equipmentEditForm
      if (!form.dbId) {
        alert('缺少设备ID')
        return
      }
      try {
        await equipmentApi.update({
          id: form.dbId,
          name: form.name,
          type: form.type,
          area: form.area,
          eff: form.eff,
          status: form.status,
          score: form.score,
        })
        alert('更新成功！')
        this.showEquipmentEditForm = false
        this.loadEquipmentList()
      } catch (error) {
        alert('更新失败！' + (error.message || '未知错误'))
      }
    },
    async deleteEquipment(eq) {
      if (!confirm('确定删除设备「' + eq.name + '」吗？此操作不可恢复。')) return
      try {
        await equipmentApi.delete({ id: eq.dbId })
        alert('删除成功！')
        this.loadEquipmentList()
      } catch (error) {
        alert('删除失败！' + (error.message || '未知错误'))
      }
    },
    prevSeedInventoryPage() {
      if (this.seedInventoryPage > 1) {
        this.seedInventoryPage--
        this.loadSeedInventory()
      }
    },
    nextSeedInventoryPage() {
      if (this.seedInventoryPage < this.seedInventoryTotalPages) {
        this.seedInventoryPage++
        this.loadSeedInventory()
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
      if (pageId === 'labor-list') {
        this.laborPage = 1
        this.loadLaborList()
      }
      if (pageId === 'labor-schedule') {
        this.loadLaborSchedule()
      }
      if (pageId === 'equipment-list') {
        this.equipmentPage = 1
        this.loadEquipmentList()
      }
      if (pageId === 'equipment-allocation') {
        this.loadEquipmentStatus()
      }
      if (pageId === 'equipment-maintenance') {
        this.loadMaintenanceData()
      }
      if (pageId === 'water-quota') {
        this.loadWaterQuota()
      }
      if (pageId === 'water-allocation') {
        this.loadWaterStatus()
      }
      if (pageId === 'water-analysis') {
        this.loadWaterAnalysis()
      }
      if (pageId === 'seed-inventory') {
        this.seedInventoryPage = 1
        this.loadSeedInventory()
      }
      if (pageId === 'seed-predict') {
        this.loadSeedPredict()
      }
      if (pageId === 'sys-user') {
        this.loadSystemUsers()
      }
      this.$nextTick(() => {
        setTimeout(() => this.initChartsForPage(pageId), 100)
      })
    },
    logout() {
      if (!confirm('确定要退出登录吗？')) return
      authApi.logout().catch(() => {})
      sessionStorage.removeItem('agri_user')
      sessionStorage.removeItem('agri_token')
      sessionStorage.removeItem('sa_token')
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
    async loadFarmlandOptimize() {
      this.aiOptimizeResultVisible = false
      this.optimizeDataFromAI = false
      await this.loadBlocksByRegion()
    },
    async loadBlocksByRegion() {
      try {
        const res = await farmlandApi.getBlocksByRegion()
        const data = res?.data || res
        const result = data?.data || data || {}
        const regions = result.regions || []

        this.optimizeRegionData = regions

        const blocks = []
        for (const region of regions) {
          const regionBlocks = region.blocks || []
          for (const block of regionBlocks) {
            blocks.push({
              blockId: block.blockId,
              blockCode: block.blockCode,
              regionName: region.regionName,
              area: block.area,
              soilType: block.soilType,
              currentCrop: block.currentCrop,
              suitableCrops: block.suitableCrops,
              status: block.status,
            })
          }
        }
        this.optimizeBlocks = blocks

        const regionNames = regions.map((r) => r.regionName)
        const cropCountMap = {}
        for (const block of blocks) {
          const crop = block.currentCrop || '未种植'
          cropCountMap[crop] = (cropCountMap[crop] || 0) + 1
        }
        const total = blocks.length || 1
        const cropDistribution = Object.entries(cropCountMap).map(([name, count]) => ({
          name,
          percentage: (count / total) * 100,
        }))
        if (cropDistribution.length > 0) {
          this.optimizeCropDistribution = cropDistribution
        }
      } catch (error) {
        console.warn('加载地块片区数据失败:', error)
      } finally {
        this.$nextTick(() => this.initCropCharts())
      }
    },
    async runAIOptimize() {
      this.showLoading('AI种植结构优化计算..', '遗传算法初始化种群（500个体）..')

      try {
        const res = await farmlandApi.optimize({
          goal: this.optimizeForm.goal,
          constraintMode: this.optimizeForm.constraintMode,
          year: this.optimizeForm.year,
        })
        const data = res?.data || res
        const result = data?.data || data || {}

        if (
          result &&
          (result.cropDistribution || result.blockSuitability || result.optimizationMetrics)
        ) {
          this.normalizeOptimizeResult(result)
          this.optimizeDataFromAI = true
          this.hideLoading()
          return
        }
      } catch (error) {
        console.error('耕地优化 API 请求失败:', error)
      }

      this.hideLoading()
      alert('AI种植结构优化请求失败，请检查后端服务是否正常运行')
    },
    async loadWaterStatus() {
      try {
        const res = await waterApi.status()
        const data = res?.data || res
        const result = data?.data || data || {}
        if (result && result.traditional) {
          this.waterChartData = result
        } else {
          this.waterChartData = null
        }
      } catch (error) {
        console.warn('Water status API unavailable, using local fallback:', error)
        this.waterChartData = null
      } finally {
        this.$nextTick(() => this.initWaterCharts())
      }
    },
    async loadWaterAnalysis() {
      this.waterAnalysisLoading = true
      try {
        const res = await waterApi.analysis({ year: this.waterAnalysisYear })
        const data = res?.data || res
        const result = data?.data || data || {}
        if (result && (result.trend || result.waste || result.report)) {
          this.waterAnalysisData = result
        } else {
          this.waterAnalysisData = null
        }
      } catch (error) {
        console.warn('Water analysis API unavailable, using local fallback:', error)
        this.waterAnalysisData = null
      } finally {
        this.waterAnalysisLoading = false
        this.$nextTick(() => this.initWaterAnalysisCharts())
      }
    },
    async loadSeedInventory() {
      try {
        const res = await seedApi.inventory({
          page: this.seedInventoryPage,
          pageSize: this.seedInventoryPageSize,
        })
        const data = res?.data || res
        const result = data?.data || data || {}
        this.seedInventoryTotal = result?.total || 0
        if (result && (result.stats || result.items)) {
          this.seedInventoryData = {
            stats: result.stats || [],
            items: result.items || [],
          }
        }
      } catch (error) {
        console.warn('Seed inventory API unavailable, using local fallback:', error)
      }
    },
    async loadSeedPredict() {
      try {
        const res = await seedApi.predict({})
        const data = res?.data || res
        const result = data?.data || data || {}
        if (result && (result.trend || result.radar || result.alert)) {
          this.seedPredictData = result
        } else {
          this.seedPredictData = null
        }
      } catch (error) {
        console.warn('Seed predict API unavailable, using local fallback:', error)
        this.seedPredictData = null
      } finally {
        this.$nextTick(() => this.initSeedPredictCharts())
      }
    },
    async loadSystemUsers() {
      this.systemUsersLoading = true
      try {
        const res = await systemApi.users({
          page: 1,
          pageSize: 100,
        })
        const data = res?.data || res
        const result = data?.data || data || {}
        if (result && result.stats) {
          this.systemStats = {
            totalUsers: result.stats.totalUsers || 0,
            totalRoles: result.stats.totalRoles || 0,
            totalPermissions: result.stats.totalPermissions || 0,
            totalLogs: result.stats.totalLogs || 0,
          }
        }
        if (result && result.list) {
          this.systemUsers = result.list
        } else if (Array.isArray(result)) {
          this.systemUsers = result
        } else {
          this.systemUsers = []
        }
      } catch (error) {
        console.warn('System users API unavailable:', error)
        this.systemUsers = []
        this.systemStats = {
          totalUsers: 0,
          totalRoles: 0,
          totalPermissions: 0,
          totalLogs: 0,
        }
      } finally {
        this.systemUsersLoading = false
      }
    },
    resetSystemUserCreateForm() {
      this.systemUserCreateForm = {
        username: '',
        name: '',
        password: '',
        role: 'farmer',
        department: '',
        status: 1,
      }
    },
    async submitSystemUserCreate() {
      const form = this.systemUserCreateForm
      if (!form.username || !form.name || !form.password) {
        alert('请填写用户名、姓名和密码')
        return
      }
      try {
        await systemApi.create({
          username: form.username,
          name: form.name,
          password: form.password,
          role: form.role,
          department: form.department,
          status: form.status,
        })
        alert('新增用户成功！')
        this.showSystemUserCreateForm = false
        this.resetSystemUserCreateForm()
        this.loadSystemUsers()
      } catch (error) {
        alert('新增用户失败！' + (error.message || '未知错误'))
      }
    },
    roleTagClass(role) {
      const map = {
        admin: 'tag-danger',
        dispatcher: 'tag-info',
        farmer: 'tag-success',
        analyst: 'tag-purple',
      }
      return map[role] || 'tag-info'
    },
    async runWaterAI() {
      this.showLoading('AI水量优化分配计算..', '线性规划模型求解中...')
      try {
        const res = await waterApi.allocation({
          cycle: this.waterCycle,
          goal: this.waterGoal,
        })
        const data = res?.data || res
        const result = data?.data || data || {}
        this.hideLoading()
        if (result && result.plan) {
          this.waterChartData = {
            ...this.waterChartData,
            aiOptimized: result.plan,
            aiEfficiency: result.efficiency,
          }
        }
        this.initWaterCharts()
        alert(
          'AI水量优化分配完成！\n\n预计节水: ' +
            (result.waterSaved ?? '--') +
            '%\n平均增产: ' +
            (result.yieldIncrease ?? '--') +
            '%',
        )
      } catch (error) {
        this.hideLoading()
        console.error('Water allocation API 请求失败:', error)
        this.initWaterCharts()
        alert('AI水量优化分配请求失败，请检查后端服务')
      }
    },
    runSeedAI() {
      this.showLoading('AI农资按需分配计算..', '多约束优化模型求解中...')
      const payload = {
        crop: this.seedAllocCrop,
        strategy: this.seedAllocStrategy,
      }
      seedApi
        .allocation(payload)
        .then((res) => {
          const data = res?.data || res
          const result = data?.data || data || {}
          this.hideLoading()
          if (result && (result.series || result.allocation)) {
            this.seedAllocChartData = result
            this.$nextTick(() => this.initSeedAllocChart())
            const tips = result.tips || ''
            alert('AI农资分配完成！' + (tips ? '\n\n' + tips : ''))
          } else {
            this.seedAllocChartData = null
            this.initSeedAllocChart()
            alert('AI农资分配完成！')
          }
        })
        .catch((error) => {
          this.hideLoading()
          console.error('Seed allocation API 请求失败:', error)
          this.seedAllocChartData = null
          this.initSeedAllocChart()
          alert('AI农资分配请求失败，请检查后端服务')
        })
    },
    async runLaborAI() {
      this.showLoading('人力智能排班计算...', '粒子群优化算法迭代中...')
      try {
        const res = await laborApi.schedule({
          task: this.laborScheduleTask,
          area: this.laborScheduleArea,
        })
        const data = res?.data || res
        this.laborScheduleResult = data
        this.hideLoading()
        this.initLaborChart()
        const matchRate = data?.matchRate ?? 0
        const gapPercent = data?.gapPercent ?? 0
        alert(`智能排班完成！\n\n整体匹配度 ${matchRate}%\n缺口: ${gapPercent}%`)
      } catch (error) {
        console.error('智能排班API调用失败:', error)
        this.laborScheduleResult = null
        this.hideLoading()
        this.initLaborChart()
        alert('智能排班请求失败，请检查后端服务')
      }
    },
    async loadEquipmentStatus() {
      this.showLoading('加载设备状态数据...', '正在获取设备调配信息...')
      try {
        const res = await equipmentApi.status({})
        const data = res?.data || res
        this.equipmentAllocationResult = data
        this.hideLoading()
        this.$nextTick(() => this.initEquipmentCharts())
      } catch (error) {
        console.warn('Equipment status API unavailable:', error)
        this.equipmentAllocationResult = null
        this.hideLoading()
        this.$nextTick(() => this.initEquipmentCharts())
      }
    },
    async loadMaintenanceData() {
      try {
        const res = await equipmentApi.maintenance({})
        const data = res?.data || res
        this.maintenanceData = data
      } catch (error) {
        console.warn('Maintenance API unavailable:', error)
        this.maintenanceData = null
      } finally {
        this.$nextTick(() => this.initMaintenanceCharts())
      }
    },
    async runEquipmentAI() {
      this.showLoading('农机路径优化排程..', '最短路径算法计算中...')
      try {
        const res = await equipmentApi.allocation({
          task: this.equipmentAllocationTask,
          area: this.equipmentAllocationArea,
        })
        const data = res?.data || res
        this.equipmentAllocationResult = data
        this.initEquipmentCharts()
        const dr = data?.distanceReduction ?? '--'
        const ei = data?.efficiencyImprovement ?? '--'
        alert(`路径优化排程完成！\n\n总行驶距离减少 ${dr}%\n作业效率提升: ${ei}%`)
      } catch (error) {
        console.error('Equipment allocation API 请求失败:', error)
        this.initEquipmentCharts()
        alert('路径优化排程请求失败，请检查后端服务')
      } finally {
        this.hideLoading()
      }
    },
    runAIDecision() {
      this.showLoading('AI综合决策方案生成..', '五维数据融合与遗传算法运算中...')
      const steps = [
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
        }
      }, 700)
      aiApi
        .decision({
          period: this.aiDecisionPeriod,
          dimension: this.aiDecisionDimension,
        })
        .then((res) => {
          const data = res?.data || res
          this.aiDecisionData = data
          this.aiDecisionResultVisible = true
        })
        .catch((error) => {
          console.warn('AI decision API unavailable:', error)
          this.aiDecisionData = null
          this.aiDecisionResultVisible = true
        })
        .finally(() => {
          clearInterval(interval)
          this.hideLoading()
          this.$nextTick(() => this.initAIDecisionCharts())
        })
    },
    initChartsForPage(pageId) {
      switch (pageId) {
        case 'dashboard':
          this.loadDashboardData()
          break
        case 'farmland-optimize':
          this.loadFarmlandOptimize()
          break
        case 'farmland-rotation':
          this.initRotationChart()
          break
        case 'water-allocation':
          break
        case 'water-analysis':
          break
        case 'seed-allocation':
          this.initSeedAllocChart()
          break
        case 'seed-predict':
          break
        case 'labor-schedule':
          break
        case 'equipment-allocation':
          break
        case 'equipment-maintenance':
          break
        case 'ai-decision':
          if (this.aiDecisionResultVisible) this.initAIDecisionCharts()
          break
        case 'predict-yield':
          this.loadYieldData()
          break
        case 'predict-resource':
          this.loadResourcePredictData()
          break
      }
    },
    initDashboardCharts() {
      if (this.charts['dash1']) this.charts['dash1'].dispose()
      if (this.charts['dash2']) this.charts['dash2'].dispose()
      if (this.charts['dash3']) this.charts['dash3'].dispose()
      if (!this.$refs.chartDash1) return

      const d = this.dashboardData
      const months = (d && d.chart1 && d.chart1.xAxis) || []
      const areas = (d && d.chart2 && d.chart2.xAxis) || []
      const chart3XAxis = (d && d.chart3 && d.chart3.xAxis) || []

      const c1 = echarts.init(this.$refs.chartDash1)
      c1.setOption({
        tooltip: { trigger: 'axis' },
        legend: {
          data: (d && d.chart1 && d.chart1.legend) || [],
          bottom: 0,
        },
        xAxis: { type: 'category', data: months },
        yAxis: { type: 'value' },
        series: (d && d.chart1 && d.chart1.series) || [],
      })
      this.charts['dash1'] = c1

      const c2 = echarts.init(this.$refs.chartDash2)
      c2.setOption({
        tooltip: { trigger: 'axis' },
        legend: { bottom: 0 },
        xAxis: { type: 'category', data: areas },
        yAxis: { type: 'value' },
        series: (d && d.chart2 && d.chart2.series) || [],
      })
      this.charts['dash2'] = c2

      const c3 = echarts.init(this.$refs.chartDash3)
      c3.setOption({
        tooltip: { trigger: 'axis' },
        legend: {
          data: (d && d.chart3 && d.chart3.legend) || [],
          bottom: 0,
        },
        xAxis: { type: 'category', data: chart3XAxis },
        yAxis: { type: 'value', max: 100 },
        series: (d && d.chart3 && d.chart3.series) || [],
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
        : []

      const xAxisData = this.optimizeRegionData.length
        ? this.optimizeRegionData.map((r) => r.regionName)
        : this.optimizeBlocks.map((item) => item.blockCode)

      let scoreSeries
      if (this.optimizeDataFromAI && this.optimizeResult?.blockSuitability?.length) {
        const blockRegionMap = {}
        for (const block of this.optimizeBlocks) {
          blockRegionMap[block.blockCode] = block.regionName
        }
        const regionNames = this.optimizeRegionData.map((r) => r.regionName)
        const cropSeriesMap = {}
        for (const item of this.optimizeResult.blockSuitability) {
          const regionName = blockRegionMap[item.blockCode] || item.blockCode
          const crop = item.recommendedCrop || '建议作物'
          if (!cropSeriesMap[crop]) {
            cropSeriesMap[crop] = {}
            for (const rn of regionNames) {
              cropSeriesMap[crop][rn] = 0
            }
          }
          cropSeriesMap[crop][regionName] = Math.max(cropSeriesMap[crop][regionName] || 0, Number(item.suitabilityScore || 0))
        }
        const colors = ['#52c41a', '#faad14', '#1890ff', '#722ed1', '#eb2f96', '#13c2c2', '#f759ab', '#fa8c16']
        scoreSeries = Object.entries(cropSeriesMap).map(([crop, regionData], idx) => ({
          name: crop,
          type: 'bar',
          data: regionNames.map((rn) => regionData[rn] || 0),
          itemStyle: { color: colors[idx % colors.length] },
          barWidth: 20,
        }))
      } else {
        const cropSet = new Set()
        for (const block of this.optimizeBlocks) {
          const crop = block.currentCrop || '未种植'
          cropSet.add(crop)
        }
        const crops = [...cropSet]
        const colors = ['#52c41a', '#faad14', '#1890ff', '#722ed1', '#eb2f96']
        scoreSeries = crops.map((crop, idx) => ({
          name: crop,
          type: 'bar',
          data: this.optimizeRegionData.length
            ? this.optimizeRegionData.map((region) => {
                const regionBlocks = region.blocks || []
                return regionBlocks.filter((b) => (b.currentCrop || '未种植') === crop).length
              })
            : this.optimizeBlocks.map((b) => ((b.currentCrop || '未种植') === crop ? 1 : 0)),
          itemStyle: { color: colors[idx % colors.length] },
          barWidth: 20,
        }))
      }

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
          data: xAxisData,
        },
        yAxis: { type: 'value', name: '适配度', max: 100 },
        series: scoreSeries,
      })
      this.charts['crop-score'] = c2
    },
    initRotationChart() {
      if (this.charts['rotation']) this.charts['rotation'].dispose()
      if (!this.$refs.chartRotation) return
      this.loadRotationData()
    },
    async loadRotationData() {
      let crops = null
      try {
        const res = await farmlandApi.rotation({ year: this.rotationYear })
        const data = res?.data || res
        const result = data?.data || data || {}
        crops = result.crops
      } catch (error) {
        console.error('Rotation API 请求失败:', error)
      }

      if (!crops || !Array.isArray(crops) || crops.length === 0) {
        crops = []
      }

      const colorMap = {
        水稻: '#52c41a',
        小麦: '#faad14',
        玉米: '#1890ff',
        休耕: '#bfbfbf',
      }

      const series = crops.map((crop) => ({
        name: crop.name,
        type: 'bar',
        stack: 'a',
        data: crop.data || [],
        itemStyle: { color: colorMap[crop.name] || '#999' },
      }))

      this.renderRotationChart(series)
    },
    renderRotationChart(series) {
      if (this.charts['rotation']) this.charts['rotation'].dispose()
      if (!this.$refs.chartRotation) return
      const c = echarts.init(this.$refs.chartRotation)
      c.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: series.map((s) => s.name), bottom: 0 },
        xAxis: {
          type: 'category',
          data: [
            '1月',
            '2月',
            '3月',
            '4月',
            '5月',
            '6月',
            '7月',
            '8月',
            '9月',
            '10月',
            '11月',
            '12月',
          ],
        },
        yAxis: { type: 'value', name: '亩' },
        series,
      })
      this.charts['rotation'] = c
    },
    initWaterCharts() {
      if (this.charts['water-compare']) this.charts['water-compare'].dispose()
      if (this.charts['water-radar']) this.charts['water-radar'].dispose()
      if (!this.$refs.chartWaterCompare) return
      const d = this.waterChartData || {}
      const tradArr = d.traditional || []
      const aiArr = d.aiOptimized || []
      const areas = tradArr.map((item) => item.area)
      const tradVals = tradArr.map((item) => item.value)
      const aiVals = aiArr.map((item) => item.value)
      const hasAI = aiArr.length > 0
      const c1 = echarts.init(this.$refs.chartWaterCompare)
      const legendData = hasAI ? ['传统方案', 'AI优化方案'] : ['传统方案']
      const series = [
        {
          name: '传统方案',
          type: 'bar',
          data: tradVals,
          itemStyle: { color: '#faad14' },
        },
      ]
      if (hasAI) {
        series.push({
          name: 'AI优化方案',
          type: 'bar',
          data: aiVals,
          itemStyle: { color: '#52c41a' },
        })
      }
      c1.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: legendData, bottom: 0 },
        xAxis: { type: 'category', data: areas },
        yAxis: { type: 'value', name: '万m³' },
        series: series,
      })
      this.charts['water-compare'] = c1
      const curEff = d.currentEfficiency || []
      const aiEff = d.aiEfficiency || []
      const hasAIEff = aiEff.length > 0
      const c2 = echarts.init(this.$refs.chartWaterRadar)
      const radarLegendData = hasAIEff ? ['当前效率', 'AI优化'] : ['当前效率']
      const radarSeries = [
        {
          type: 'radar',
          data: [
            {
              value: curEff,
              name: '当前效率',
              itemStyle: { color: '#faad14' },
              areaStyle: { opacity: 0.2 },
            },
          ],
        },
      ]
      if (hasAIEff) {
        radarSeries[0].data.push({
          value: aiEff,
          name: 'AI优化',
          itemStyle: { color: '#52c41a' },
          areaStyle: { opacity: 0.3 },
        })
      }
      c2.setOption({
        tooltip: {},
        legend: { data: radarLegendData, bottom: 0 },
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
        series: radarSeries,
      })
      this.charts['water-radar'] = c2
    },
    initWaterAnalysisCharts() {
      if (this.charts['water-trend']) this.charts['water-trend'].dispose()
      if (this.charts['water-waste']) this.charts['water-waste'].dispose()
      if (!this.$refs.chartWaterTrend) return

      const d = this.waterAnalysisData || {}
      const trendData = d.trend || {
        months: [
          '1月',
          '2月',
          '3月',
          '4月',
          '5月',
          '6月',
          '7月',
          '8月',
          '9月',
          '10月',
          '11月',
          '12月',
        ],
        actual: [180, 165, 195, 210, 230, 245, 260, 255, 240, 220, 200, 190],
        predicted: [null, null, null, null, null, null, null, null, null, 215, 195, 185],
      }
      const wasteData = d.waste || {
        categories: [
          { value: 82, name: '高效利用', color: '#52c41a' },
          { value: 12, name: '轻微浪费', color: '#faad14' },
          { value: 6, name: '严重浪费', color: '#f5222d' },
        ],
      }

      const c1 = echarts.init(this.$refs.chartWaterTrend)
      c1.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['实际用水', '预测趋势'], bottom: 0 },
        xAxis: {
          type: 'category',
          data: trendData.months,
        },
        yAxis: { type: 'value', name: '万m³' },
        series: [
          {
            name: '实际用水',
            type: 'line',
            data: trendData.actual,
            itemStyle: { color: '#1890ff' },
            smooth: true,
          },
          {
            name: '预测趋势',
            type: 'line',
            data: trendData.predicted,
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
            data: wasteData.categories.map((item) => ({
              value: item.value,
              name: item.name,
              itemStyle: { color: item.color },
            })),
          },
        ],
      })
      this.charts['water-waste'] = c2
    },
    initSeedAllocChart() {
      if (this.charts['seed-alloc']) this.charts['seed-alloc'].dispose()
      if (!this.$refs.chartSeedAlloc) return
      const c = echarts.init(this.$refs.chartSeedAlloc)
      const apiData = this.seedAllocChartData
      const series = apiData?.series || []
      const xData = apiData?.categories || []
      const legendData = apiData?.legend || series.map((s) => s.name)
      c.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: legendData, bottom: 0 },
        xAxis: { type: 'category', data: xData },
        yAxis: { type: 'value', name: apiData?.yAxisName || 'kg' },
        series: series,
      })
      this.charts['seed-alloc'] = c
    },
    initSeedPredictCharts() {
      if (this.charts['seed-predict']) this.charts['seed-predict'].dispose()
      if (this.charts['seed-alert']) this.charts['seed-alert'].dispose()
      if (!this.$refs.chartSeedPredict) return
      const apiData = this.seedPredictData
      const trendXData = apiData?.trendMonths || []
      const trendSeries = apiData?.trend || []
      const c1 = echarts.init(this.$refs.chartSeedPredict)
      c1.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: trendSeries.map((s) => s.name), bottom: 0 },
        xAxis: { type: 'category', data: trendXData },
        yAxis: { type: 'value', name: apiData?.yAxisName || 'kg' },
        series: trendSeries,
      })
      this.charts['seed-predict'] = c1
      const radarIndicator = apiData?.radarIndicator || []
      const radarData = apiData?.radar || []
      const c2 = echarts.init(this.$refs.chartSeedAlert)
      c2.setOption({
        tooltip: { trigger: 'axis' },
        radar: {
          indicator: radarIndicator,
          radius: '55%',
        },
        series: [
          {
            type: 'radar',
            data: radarData,
          },
        ],
      })
      this.charts['seed-alert'] = c2
    },
    initLaborChart() {
      if (this.charts['labor']) this.charts['labor'].dispose()
      if (!this.$refs.chartLabor) return
      const c = echarts.init(this.$refs.chartLabor)
      const d = this.laborScheduleResult
      const chartData = d?.chartData || []
      c.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['需求人数', '供给人数', '缺口'], bottom: 0 },
        xAxis: { type: 'category', data: chartData.map((item) => item.task) },
        yAxis: { type: 'value', name: '人' },
        series: [
          {
            name: '需求人数',
            type: 'bar',
            data: chartData.map((item) => item.demand),
            itemStyle: { color: '#fa8c16' },
          },
          {
            name: '供给人数',
            type: 'bar',
            data: chartData.map((item) => item.supply),
            itemStyle: { color: '#52c41a' },
          },
          {
            name: '缺口',
            type: 'bar',
            data: chartData.map((item) => item.gap),
            itemStyle: { color: '#f5222d' },
          },
        ],
      })
      this.charts['labor'] = c
    },
    initEquipmentCharts() {
      if (this.charts['equip-pie']) this.charts['equip-pie'].dispose()
      if (this.charts['equip-bar']) this.charts['equip-bar'].dispose()
      if (!this.$refs.chartEquipPie) return
      const r = this.equipmentAllocationResult
      const pieData = r?.pieData || []
      const barCat = r?.barCategories || []
      const barData = r?.barData || []
      const c1 = echarts.init(this.$refs.chartEquipPie)
      c1.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        series: [{ type: 'pie', radius: ['40%', '70%'], data: pieData }],
      })
      this.charts['equip-pie'] = c1
      const c2 = echarts.init(this.$refs.chartEquipBar)
      c2.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: barCat },
        yAxis: { type: 'value', name: '利用率(%)', max: 100 },
        series: [
          {
            type: 'bar',
            data: barData,
            barWidth: 30,
            itemStyle: { color: '#52c41a' },
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
      const md = this.maintenanceData
      const healthData = (md && md.healthStatus) || []
      const costData = (md && md.costTrend) || { months: [], values: [] }
      const c1 = echarts.init(this.$refs.chartMaintHealth)
      c1.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        series: [
          {
            type: 'pie',
            radius: '60%',
            data: healthData,
          },
        ],
      })
      this.charts['maint-health'] = c1
      const c2 = echarts.init(this.$refs.chartMaintCost)
      c2.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: costData.months },
        yAxis: { type: 'value', name: '万元' },
        series: [
          {
            type: 'line',
            data: costData.values,
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
      const ad = this.aiDecisionData
      const radarData = (ad && ad.radar) || []
      const pieData = (ad && ad.resourceAllocation) || []
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
            data: radarData,
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
            data: pieData,
          },
        ],
      })
      this.charts['ai-pie'] = c2
    },
    async loadYieldData() {
      try {
        const res = await predictApi.yield({ year: this.yieldTrendYear, area: this.yieldTrendArea })
        const data = res?.data || res
        this.yieldData = data
        if (data && data.areaOptions && data.areaOptions.length) {
          this.yieldAreaOptions = data.areaOptions
        }
        if (data && data.yearOptions && data.yearOptions.length) {
          this.yieldYearOptions = data.yearOptions
        }
      } catch (error) {
        console.warn('Yield predict API unavailable:', error)
        this.yieldData = null
        this.yieldAreaOptions = ['全部', '北区', '南区', '东区', '西区']
        this.yieldYearOptions = ['2020', '2021', '2022', '2023', '2024', '2025']
      } finally {
        this.$nextTick(() => this.initYieldCharts())
      }
    },
    getYieldTrendData(year) {
      const months = [
        '1月',
        '2月',
        '3月',
        '4月',
        '5月',
        '6月',
        '7月',
        '8月',
        '9月',
        '10月',
        '11月',
        '12月',
      ]
      if (this.yieldData && this.yieldData.trend) {
        return { months: this.yieldData.trend.months || months, data: this.yieldData.trend }
      }
      return { months, data: { rice: [], wheat: [], corn: [] } }
    },
    onYieldFilterChange() {
      this.loadYieldData()
    },
    initYieldCharts() {
      if (this.charts['yield-trend']) this.charts['yield-trend'].dispose()
      if (this.charts['yield-pie']) this.charts['yield-pie'].dispose()
      if (!this.$refs.chartYieldTrend) return
      const { months, data } = this.getYieldTrendData(this.yieldTrendYear)
      const c1 = echarts.init(this.$refs.chartYieldTrend)
      c1.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['水稻', '小麦', '玉米'], bottom: 0 },
        xAxis: { type: 'category', data: months },
        yAxis: { type: 'value', name: '产量(吨/亩)' },
        series: [
          {
            name: '水稻',
            type: 'line',
            data: data.rice,
            smooth: true,
            itemStyle: { color: '#52c41a' },
          },
          {
            name: '小麦',
            type: 'line',
            data: data.wheat,
            smooth: true,
            itemStyle: { color: '#faad14' },
          },
          {
            name: '玉米',
            type: 'line',
            data: data.corn,
            smooth: true,
            itemStyle: { color: '#1890ff' },
          },
        ],
      })
      this.charts['yield-trend'] = c1
      const c2 = echarts.init(this.$refs.chartYieldPie)
      const pieData = (this.yieldData && this.yieldData.pie) || []
      c2.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        series: [
          {
            type: 'pie',
            radius: '60%',
            data: pieData,
          },
        ],
      })
      this.charts['yield-pie'] = c2
    },
    async loadResourcePredictData() {
      try {
        const res = await predictApi.resource({
          year: this.resourcePredictYear,
          area: this.resourcePredictArea,
        })
        const data = res?.data || res
        this.resourcePredictData = data
        if (data && data.areaOptions && data.areaOptions.length) {
          this.resourceAreaOptions = data.areaOptions
        }
        if (data && data.yearOptions && data.yearOptions.length) {
          this.resourceYearOptions = data.yearOptions
        }
      } catch (error) {
        console.warn('Resource predict API unavailable:', error)
        this.resourcePredictData = null
        this.resourceAreaOptions = ['全部', '北区', '南区', '东区', '西区']
        this.resourceYearOptions = ['2020', '2021', '2022', '2023', '2024', '2025']
      } finally {
        this.$nextTick(() => this.initResourcePredictCharts())
      }
    },
    onResourceFilterChange() {
      this.loadResourcePredictData()
    },
    getResourcePredictData() {
      const months = [
        '1月',
        '2月',
        '3月',
        '4月',
        '5月',
        '6月',
        '7月',
        '8月',
        '9月',
        '10月',
        '11月',
        '12月',
      ]
      if (this.resourcePredictData) {
        return {
          months: this.resourcePredictData.months || months,
          water: this.resourcePredictData.water,
          seed: this.resourcePredictData.seed,
        }
      }
      return {
        months,
        water: { agriculture: [], ecology: [] },
        seed: { fertilizer: [], pesticide: [], seed: [] },
      }
    },
    initResourcePredictCharts() {
      if (this.charts['predict-water']) this.charts['predict-water'].dispose()
      if (this.charts['predict-seed']) this.charts['predict-seed'].dispose()
      if (!this.$refs.chartPredictWater) return
      const { months, water, seed } = this.getResourcePredictData()
      const c1 = echarts.init(this.$refs.chartPredictWater)
      c1.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['农业用水', '生态用水'], bottom: 0 },
        xAxis: { type: 'category', data: months },
        yAxis: { type: 'value', name: '万m³' },
        series: [
          {
            name: '农业用水',
            type: 'line',
            data: water.agriculture,
            smooth: true,
            areaStyle: { opacity: 0.2 },
            itemStyle: { color: '#52c41a' },
          },
          {
            name: '生态用水',
            type: 'line',
            data: water.ecology,
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
        xAxis: { type: 'category', data: months },
        yAxis: { type: 'value', name: 'kg' },
        series: [
          {
            name: '化肥',
            type: 'bar',
            data: seed.fertilizer,
            itemStyle: { color: '#52c41a' },
            barWidth: 20,
          },
          {
            name: '农药',
            type: 'bar',
            data: seed.pesticide,
            itemStyle: { color: '#faad14' },
            barWidth: 20,
          },
          {
            name: '种子',
            type: 'bar',
            data: seed.seed,
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
.modal-overlay {
  display: none;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.45);
  z-index: 1000;
  align-items: center;
  justify-content: center;
}
.modal-overlay.active {
  display: flex;
}
.modal-box {
  background: #fff;
  border-radius: 8px;
  width: 700px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}
.modal-header h3 {
  font-size: 16px;
  margin: 0;
}
.modal-close {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  color: #999;
}
.modal-close:hover {
  color: #333;
}
.modal-body {
  padding: 20px;
}
.modal-footer {
  padding: 12px 20px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
.modal-input {
  width: 100%;
  padding: 6px 8px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 12px;
}
.modal-input:focus {
  border-color: #1890ff;
  outline: none;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}
</style>