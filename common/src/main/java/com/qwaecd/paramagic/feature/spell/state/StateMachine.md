# 法术状态机
法术状态机包含以下状态：
- IDLE: 空闲状态
- CASTING: 法术正在吟唱
- CHANNELING: 法术正在施法
- COOLDOWN: 法术冷却中

## 状态转换
| state      | event      | next state |
|------------|------------|------------|
| IDLE       | 吟唱条件       | CASTING    |
| IDLE       | 立即施法条件     | CHANNELING |
| IDLE       | 其他条件       | IDLE       |
| CASTING    | 施法条件判断成功   | CHANNELING |
| CASTING    | 施法条件判断失败   | IDLE       |
| CHANNELING | 持续施法条件判断成功 | CHANNELING |
| CHANNELING | 持续施法条件判断失败 | COOLDOWN   |
| COOLDOWN   | 冷却结束       | IDLE       |

### 说明
- 吟唱条件：法术配置为需要吟唱
- 立即施法条件：法术配置为无需吟唱
- 其他条件：非 吟唱条件/立即施法条件 的情况
- 施法条件判断成功：如 法术完成吟唱
- 施法条件判断失败：如 法术未完成吟唱、法术被打断
- 持续施法条件判断成功：如 法术配置为持续施法
- 持续施法条件判断失败：如 法术配置为非持续施法、法术被打断
- 冷却结束：法术冷却结束

初始状态一般为 IDLE。

法术被打断有多种情况，比如：
- 受到攻击
- 玩家取消施法
- 法术配置中声明玩家移动打断施法 -> 可以抽象为配置判据ConfigBooleanFunction()
