package com.panduoma.demo.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.panduoma.demo.entity.FarmlandBlock;
import com.panduoma.demo.entity.FarmlandOptimizeRequest;
import com.panduoma.demo.entity.LoginDTO;
import com.panduoma.demo.entity.User;
import com.panduoma.demo.response.Result;
import com.panduoma.demo.service.DashboardService;
import com.panduoma.demo.service.FarmlandService;
import com.panduoma.demo.service.EquipmentService;
import com.panduoma.demo.service.LaborService;
import com.panduoma.demo.service.SeedService;
import com.panduoma.demo.service.UserService;
import com.panduoma.demo.service.WaterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

//POST /auth/login   登录
//POST /auth/me      获取当前用户信息
//POST /auth/logout   登出
//POST /dashboard/overview   获取看板数据

//POST /farmland/list     获取所有农田
//POST /farmland/create   创建农田
//POST /farmland/optimize  ai优化农田布局
//POST /farmland/rotation  休耕计划

//POST /water/status
//POST /water/quota     水位配额
//POST /water/quota/update    配置调整
//POST /water/allocation    ai水位分配
//POST /water/analysis    水位分析
//POST /seed/inventory    种子库存
//POST /seed/create       入库登记
//POST /seed/allocation  ai需求分配
//POST /seed/predict   消耗预测与库存预警
//POST /labor/list    劳动力列表
//POST /labor/create    新增劳动力
//POST /labor/old/schedule  劳动力排班
//POST /labor/schedule  智能排班调度
//POST /equipment/list  农机具台账管理
//POST /equipment/create  新增设备
//POST /equipment/status  原始设备调配分布
//POST /equipment/allocation   智能调配与路径优化
//POST /equipment/maintenance    维护保养管理
//POST /ai/decision    AI综合决策中心
//POST /predict/yield   作物产量预测
//POST /predict/resource   资源需求智能预测
//POST /system/users    系统用户管理
//POST /system/create   创建用户

@Tag(name = "农业管理系统")
@RestController
@RequestMapping("/api")
public class Controller {

    @Resource
    private UserService userService;

    @Resource
    private FarmlandService farmlandService;

    @Resource
    private WaterService waterService;

    @Resource
    private SeedService seedService;

    @Resource
    private LaborService laborService;

    @Resource
    private EquipmentService equipmentService;

    @Resource
    private DashboardService dashboardService;

    @Operation(summary = "用户登录")
    @PostMapping("/auth/login")
    public Result<?> login(@RequestBody LoginDTO loginDTO){
        Result<?> res = userService.login(loginDTO);
        System.out.println("=== 登录响应: code=" + res.getCode() + ", message=" + res.getMessage() + " ===");
        if (res.getCode() == 200){
            User loginUser = (User) res.getData();
            StpUtil.login(loginUser.getId());
            String token = StpUtil.getTokenValue();
            Map<String, Object> data = new HashMap<>();
            data.put("user", loginUser);
            data.put("token", token);
            return Result.data(data);
        }
        return res;
    }

    @Operation(summary = "获取当前用户信息")
    @PostMapping("/auth/me")
    public Result<?> me(){
        Long uId = StpUtil.getLoginIdAsLong();
        return userService.getUserById(uId);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/auth/logout")
    public Result<?> logout(@RequestBody User user){
        System.out.println("=== 登出请求: id=" + user.getId() + ", username=" + user.getUsername() + ", role=" + user.getRole() + " ===");
        Result<?> result = userService.logout(user);
        if (user.getId() != null) {
            StpUtil.logout(user.getId());
        }
        return result;
    }

    @Operation(summary = "获取看板数据")
    @PostMapping("/dashboard/overview")
    public Result<?> dashboardOverview() {
        return dashboardService.dashboardOverview();
    }

    @Operation(summary = "地块台账")
    @PostMapping("/farmland/list")
    public Result<?> farmlandList(@RequestBody FarmlandListRequest request) {
        int page = request != null && request.getPage() != null ? Math.max(1, request.getPage()) : 1;
        int size = request != null && request.getPageSize() != null ? Math.max(1, request.getPageSize()) : 20;
        return farmlandService.farmlandList(page, size);
    }

    @Operation(summary = "创建耕地地块")
    @PostMapping("/farmland/create")
    public Result<?> farmlandCreate(@RequestBody FarmlandBlock farmlandBlock) {
        return farmlandService.farmlandCreate(farmlandBlock);
    }

    @Operation(summary = "耕地 AI 优化结果写入")
    @PostMapping("/farmland/optimize")
    public Result<?> farmlandOptimize(@RequestBody FarmlandOptimizeRequest request) {
        return farmlandService.farmlandOptimize(request);
    }

    @Operation(summary = "获取地块轮作历史数据")
    @PostMapping("/farmland/rotation")
    public Result<?> farmlandRotation(@RequestBody Map<String, Object> request) {
        Integer year = request != null && request.get("year") != null ? ((Number) request.get("year")).intValue() : null;
        return farmlandService.farmlandRotation(year);
    }

    @Operation(summary = "水位配额")
    @PostMapping("/water/quota")
    public Result<?> waterQuota(@RequestBody(required = false) FarmlandListRequest request) {
        int page = request != null && request.getPage() != null ? Math.max(1, request.getPage()) : 1;
        int size = request != null && request.getPageSize() != null ? Math.max(1, request.getPageSize()) : 20;
        return waterService.waterQuota(page, size);
    }

    @Operation(summary = "传统水位管理")
    @PostMapping("/water/status")
    public Result<?> waterStatus() {
        return waterService.waterStatus();
    }
    @Operation(summary = "AI 水位管理结果")
    @PostMapping("/water/allocation")
    public Result<?> waterAllocation(@RequestBody Map<String, Object> request) {
        return waterService.waterAllocation(request);
    }
    @Operation(summary = "水位分析")
    @PostMapping("/water/analysis")
    public Result<?> waterAnalysis(@RequestBody Map<String, Object> request) {
        int year = request != null && request.get("year") != null ? ((Number) request.get("year")).intValue() : 0;
        return waterService.waterAnalysis(year);
    }

    @Operation(summary = "水位配额配置调整")
    @PostMapping("/water/quota/update")
    public Result<?> waterQuotaUpdate(@RequestBody Map<String, Object> request) {
        return waterService.waterQuotaUpdate(request);
    }
    @Operation(summary = "农资库存")
    @PostMapping("/seed/inventory")
    public Result<?> seedInventory(@RequestBody(required = false) FarmlandListRequest request) {
        int page = request != null && request.getPage() != null ? Math.max(1, request.getPage()) : 1;
        int size = request != null && request.getPageSize() != null ? Math.max(1, request.getPageSize()) : 20;
        return seedService.seedInventory(page, size);
    }

    @Operation(summary = "入库登记")
    @PostMapping("/seed/create")
    public Result<?> seedCreate(@RequestBody Map<String, Object> request) {
        return seedService.seedCreate(request);
    }

    @Operation(summary = "AI 需求分配")
    @PostMapping("/seed/allocation")
    public Result<?> seedAllocation(@RequestBody Map<String, Object> request) {
        return seedService.seedAllocation(request);
    }

    @Operation(summary = "消耗预测与库存预警")
    @PostMapping("/seed/predict")
    public Result<?> seedPredict() {
        return seedService.seedPredict();
    }
    @Operation(summary = "劳动力列表")
    @PostMapping("/labor/list")
    public Result<?> laborList(@RequestBody(required = false) FarmlandListRequest request) {
        int page = request != null && request.getPage() != null ? Math.max(1, request.getPage()) : 1;
        int size = request != null && request.getPageSize() != null ? Math.max(1, request.getPageSize()) : 20;
        return laborService.laborList(page, size);
    }

    @Operation(summary = "劳动力排班")
    @PostMapping("/labor/old/schedule")
    public Result<?> laborOldSchedule(@RequestBody Map<String, Object> request) {
        return laborService.laborOldSchedule(request);
    }

    @Operation(summary = "智能排班调度")
    @PostMapping("/labor/schedule")
    public Result<?> laborSchedule(@RequestBody Map<String, Object> request) {
        return laborService.laborSchedule(request);
    }

    @Operation(summary = "新增劳动力")
    @PostMapping("/labor/create")
    public Result<?> laborCreate(@RequestBody Map<String, Object> request) {
        return laborService.laborCreate(request);
    }

    @Operation(summary = "农机具台账")
    @PostMapping("/equipment/list")
    public Result<?> equipmentList(@RequestBody(required = false) FarmlandListRequest request) {
        int page = request != null && request.getPage() != null ? Math.max(1, request.getPage()) : 1;
        int size = request != null && request.getPageSize() != null ? Math.max(1, request.getPageSize()) : 20;
        return equipmentService.equipmentList(page, size);
    }

    @Operation(summary = "新增设备")
    @PostMapping("/equipment/create")
    public Result<?> equipmentCreate(@RequestBody Map<String, Object> request) {
        return equipmentService.equipmentCreate(request);
    }
    @Operation(summary = "设备调配分布")
    @PostMapping("/equipment/status")
    public Result<?> equipmentStatus() {
        return equipmentService.equipmentStatus();
    }

    @Operation(summary = "智能调配与路径优化")
    @PostMapping("/equipment/allocation")
    public Result<?> machineryDispatchCreate(@RequestBody Map<String, Object> request) {
        return equipmentService.machineryDispatchCreate(request);
    }

    @Operation(summary = "维护保养管理")
    @PostMapping("/equipment/maintenance")
    public Result<?> equipmentMaintenance() {
        return equipmentService.equipmentMaintenance();
    }

    @Operation(summary = "AI 决策")
    @PostMapping("/ai/decision")
    public Result<?> aiDecision(@RequestBody Map<String, Object> request) {
        return equipmentService.aiDecision(request);
    }

    @Operation(summary = "作物产量预测")
    @PostMapping("/predict/yield")
    public Result<?> yieldPrediction(@RequestBody Map<String, Object> request) {
        return equipmentService.yieldPrediction(request);
    }

    @Operation(summary = "资源需求智能预测")
    @PostMapping("/predict/resource")
    public Result<?> resourcePrediction(@RequestBody Map<String, Object> request) {
        return equipmentService.resourcePrediction(request);
    }
}