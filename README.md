# BillBook 记账 App

一款简洁好用的 Android 记账应用，使用 Kotlin + Jetpack Compose 开发。

## 功能特性

### 核心功能
- ✅ 记录收入/支出
- ✅ 分类管理（支持自定义分类）
- ✅ 数据统计与图表（饼图、进度条）
- ✅ 预算设置与追踪
- ✅ 数据备份与恢复（JSON/CSV）
- ✅ 多币种支持（人民币、美元、欧元、日元、港币、英镑）

### 技术栈
- **UI**: Jetpack Compose + Material Design 3
- **架构**: MVVM + Repository 模式
- **依赖注入**: Hilt
- **本地存储**: Room 数据库
- **异步处理**: Kotlin Coroutines + Flow

## 项目结构

```
BillBook/
├── app/src/main/java/com/billbook/
│   ├── data/
│   │   ├── dao/          # Room DAO 接口
│   │   ├── database/     # 数据库定义
│   │   ├── model/        # 数据实体
│   │   └── repository/   # 数据仓库
│   ├── di/               # 依赖注入模块
│   ├── export/           # 数据导出功能
│   ├── ui/
│   │   ├── components/   # 可复用组件
│   │   ├── screens/      # 页面
│   │   ├── theme/        # 主题配置
│   │   └── viewmodel/    # ViewModel
│   ├── utils/            # 工具类
│   ├── MainActivity.kt
│   └── BillBookApplication.kt
```

## 开始使用

### 环境要求
- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17+
- Android SDK 34

### 运行项目
1. 使用 Android Studio 打开项目
2. 同步 Gradle
3. 连接设备或启动模拟器
4. 点击 Run

### 构建 APK
```bash
./gradlew assembleRelease
```

## 功能截图

### 首页
- 月度收支汇总卡片
- 交易记录列表
- 快速添加按钮

### 记账
- 收入/支出切换
- 金额输入
- 分类选择（图标+颜色）
- 货币选择
- 备注

### 统计
- 饼图展示分类占比
- 月度趋势
- 分类明细列表

### 预算
- 总预算设置
- 分类预算
- 进度追踪
- 超支提醒

### 设置
- 默认货币设置
- 数据备份（JSON）
- 导出 CSV
- 数据恢复

## 数据库设计

### 实体
- **Transaction**: 交易记录
- **Category**: 分类（收入/支出）
- **Budget**: 预算
- **ExchangeRate**: 汇率

## 开源协议

MIT License
