# BillBook 记账 App 完整项目

## 项目结构

```
BillBook/
├── app/
│   ├── src/main/java/com/billbook/
│   │   ├── data/
│   │   │   ├── dao/              # Room DAO 接口
│   │   │   │   ├── TransactionDao.kt
│   │   │   │   ├── CategoryDao.kt
│   │   │   │   ├── BudgetDao.kt
│   │   │   │   └── CurrencyDao.kt
│   │   │   ├── database/         # 数据库
│   │   │   │   ├── AppDatabase.kt
│   │   │   │   └── DatabaseSeeder.kt
│   │   │   ├── model/            # 数据实体
│   │   │   │   └── Entities.kt
│   │   │   └── repository/       # 数据仓库
│   │   │       ├── TransactionRepository.kt
│   │   │       ├── BudgetRepository.kt
│   │   │       └── CategoryRepository.kt
│   │   ├── di/
│   │   │   └── DatabaseModule.kt
│   │   ├── export/
│   │   │   └── DataExporter.kt   # 数据导出功能
│   │   ├── ui/
│   │   │   ├── components/       # UI 组件
│   │   │   │   ├── Charts.kt
│   │   │   │   └── CommonComponents.kt
│   │   │   ├── screens/          # 页面
│   │   │   │   ├── HomeScreen.kt
│   │   │   │   ├── AddTransactionScreen.kt
│   │   │   │   ├── StatisticsScreen.kt
│   │   │   │   ├── BudgetScreen.kt
│   │   │   │   └── SettingsScreen.kt
│   │   │   ├── theme/            # 主题
│   │   │   ├── viewmodel/        # ViewModel
│   │   │   └── Navigation.kt
│   │   ├── utils/
│   │   │   └── FormatUtils.kt
│   │   ├── MainActivity.kt
│   │   └── BillBookApplication.kt
│   └── src/main/res/             # 资源文件
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/
    └── libs.versions.toml        # 版本管理
```

## 技术栈

| 技术 | 用途 |
|------|------|
| Kotlin | 编程语言 |
| Jetpack Compose | UI 框架 |
| Room | 本地数据库 |
| Hilt | 依赖注入 |
| Coroutines/Flow | 异步处理 |
| Material Design 3 | UI 组件库 |

## 功能清单

### 已实现 ✅
- [x] 记录收入/支出
- [x] 分类管理（内置8个支出分类，5个收入分类）
- [x] 数据统计与图表（饼图、进度条）
- [x] 预算设置（总预算+分类预算）
- [x] 数据备份/恢复（JSON格式）
- [x] CSV 导出
- [x] 多币种支持（6种货币）
- [x] 月度切换
- [x] 交易记录编辑/删除

### 页面
1. **首页** - 月度汇总 + 交易列表
2. **记账** - 添加收入/支出记录
3. **统计** - 饼图 + 分类分析
4. **预算** - 预算设置与进度追踪
5. **设置** - 货币设置 + 数据管理

## 运行说明

```bash
# 克隆项目后
cd BillBook

# Linux/Mac
./gradlew assembleDebug

# Windows
gradlew.bat assembleDebug
```

然后用 Android Studio 打开，连接设备运行。
