# Jackson View Extension Spring Boot Starter

[![Maven Central](https://img.shields.io/maven-central/v/io.github.vennarshulytz/json-view-ext-spring-boot-starter.svg)](https://search.maven.org/artifact/io.github.vennarshulytz/json-view-ext-spring-boot-starter)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/Java-8%2B-orange.svg)](https://www.oracle.com/java/)

##### [📖 English Documentation](README.md) | 📖 中文文档

一个基于 Jackson 的 Spring Boot Starter，提供比 `@JsonView` 更灵活的 JSON 序列化字段控制能力，支持细粒度的字段过滤和敏感数据脱敏。

## 🔗 项目地址

- **GitHub**：[vennarshulytz/json-view-ext-parent: A Spring Boot Starter for custom JSON serialization with field filtering and sensitive data masking](https://github.com/vennarshulytz/json-view-ext-parent)

---

- 如果在使用过程中遇到问题，欢迎随时提交 Issue；也非常欢迎通过 PR 参与改进。 
- 如果这个项目对你有所帮助，欢迎在 GitHub 上点个 ⭐ Star 支持一下。
- 你的支持是开源作者持续维护和迭代项目的重要动力！

## 📖 项目介绍

在 Spring Boot 项目中，Controller 层返回的数据往往需要根据不同场景进行定制化处理。传统的 `@JsonView` 注解虽然能实现视图控制，但在复杂场景下显得不够灵活。本项目提供了一套更强大的解决方案：

- **细粒度字段控制**：支持按类型、按路径精确控制序列化字段
- **嵌套路径支持**：通过 `.` 分隔符定位嵌套对象中的特定字段
- **敏感数据脱敏**：内置脱敏处理器，支持自定义扩展
- **优先级机制**：include 优先级高于 exclude，规则清晰明确
- **零侵入设计**：无需修改现有实体类，仅需在 Controller 方法上添加注解
- **多版本支持**：兼容 Spring Boot 1.x、2.x 和 3.x

## 🎯 项目背景

在公司项目升级改造过程中，我们发现 Controller 层返回全量数据存在以下问题：

1. **性能影响**：大量无用字段增加网络传输开销
2. **安全隐患**：敏感信息可能被意外暴露
3. **维护成本**：通过定义 VO 实体进行改造过于繁琐

为了解决这些问题，我们开发了这个项目作为中间过渡方案，让您可以：

- **快速控制**返回字段，无需创建大量 VO 类
- **平滑过渡**到标准 VO 模式，降低改造风险
- **灵活配置**不同接口的返回内容

### 🔌 推荐搭配：FastConvert IDEA 插件

在后续改造成 VO 实体的过程中，推荐使用我们开发的 IDEA 插件 **[FastConvert](https://plugins.jetbrains.com/plugin/28433-fastconvert)**，它可以帮助您：

- 一键生成对象转换代码
- 智能匹配字段映射关系
- 相比 `BeanUtils`、`MapStruct` 更加便捷高效

👉 [插件详细介绍](https://plugins.jetbrains.com/plugin/28433-fastconvert/about)

##  🔄 版本兼容性

| Starter 模块                         | Spring Boot 版本 | JDK 版本 | Servlet API |
| ------------------------------------ | ---------------- | -------- | ----------- |
| `json-view-ext-spring-boot-starter`  | 1.x / 2.x        | 8+       | javax       |
| `json-view-ext-spring-boot3-starter` | 3.x              | 17+      | jakarta     |

## 🚀 快速开始

### 1. 添加依赖

根据您的 Spring Boot 版本选择合适的 Starter：

#### Spring Boot 1.x / Spring Boot 2.x（JDK 8+）

**Maven:**

```xml
<dependency>
    <groupId>io.github.vennarshulytz</groupId>
    <artifactId>json-view-ext-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Gradle:**
```groovy
implementation 'io.github.vennarshulytz:json-view-ext-spring-boot-starter:1.0.0'
```

#### Spring Boot 3.x（JDK 17+）

**Maven:**

```xml
<dependency>
    <groupId>io.github.vennarshulytz</groupId>
    <artifactId>json-view-ext-spring-boot3-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Gradle:**

```groovy
implementation 'io.github.vennarshulytz:json-view-ext-spring-boot3-starter:1.0.0'
```

### 2. 启用功能

在 Spring Boot 启动类上添加 `@EnableJsonViewExt` 注解：

```java
@SpringBootApplication
@EnableJsonViewExt
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## 📚 使用说明

### 实体类定义

以下示例中使用的实体类：

```java
@Data
public class Department {
    private String name;
    private Employee manager1;
    private List<Employee> managerList1;
}

@Data
public class Employee {
    private String id;
    private String name;
    private String number;
    private Address address1;
    private List<Address> addressList1;
}

@Data
public class Address {
    private String id;
    private String province;
    private String city;
}
```

---

### 基础使用

#### 场景一：Include - 仅包含指定字段

只返回 `Employee` 类的 `name` 和 `number` 字段：

```java
@GetMapping("/employee")
@JsonViewExt(
    include = {
        @JsonFilterExt(clazz = Employee.class, props = {"name", "number"})
    }
)
public Employee getEmployee() {
    return employeeService.findById("1");
}
```

**不加注解时的返回结果：**
```json
{
    "id": "9ccb342f-18f2-46c1-872e-892c0c3f6d14",
    "name": "张三",
    "number": "EMP001",
    "address1": {
        "id": "a7ab2748-68ec-4e52-97c3-a241317ef347",
        "province": "广东省",
        "city": "深圳市"
    },
    "addressList1": [
        {
            "id": "c3b735e8-c8c7-4aab-b8e0-fddab5bcbe2e",
            "province": "广东省",
            "city": "广州市"
        }
    ]
}
```

**加注解后的返回结果：**
```json
{
    "name": "张三",
    "number": "EMP001"
}
```

---

#### 场景二：Exclude - 排除指定字段

返回 `Address` 类时排除 `id` 字段：

```java
@GetMapping("/address")
@JsonViewExt(
    exclude = {
        @JsonFilterExt(clazz = Address.class, props = {"id"})
    }
)
public Address getAddress() {
    return addressService.findById("1");
}
```

**不加注解时的返回结果：**
```json
{
    "id": "a7ab2748-68ec-4e52-97c3-a241317ef347",
    "province": "广东省",
    "city": "深圳市"
}
```

**加注解后的返回结果：**
```json
{
    "province": "广东省",
    "city": "深圳市"
}
```

---

#### 场景三：多类型组合控制

同时控制多个类型的序列化字段：

```java
@GetMapping("/department")
@JsonViewExt(
    include = {
        @JsonFilterExt(clazz = Department.class, props = {"name", "manager1"}),
        @JsonFilterExt(clazz = Employee.class, props = {"name", "number"})
    },
    exclude = {
        @JsonFilterExt(clazz = Address.class, props = {"id"})
    }
)
public Department getDepartment() {
    return departmentService.findById("1");
}
```

**不加注解时的返回结果：**
```json
{
    "name": "技术部",
    "manager1": {
        "id": "emp-001",
        "name": "张三",
        "number": "EMP001",
        "address1": {
            "id": "addr-001",
            "province": "广东省",
            "city": "深圳市"
        },
        "addressList1": []
    },
    "managerList1": []
}
```

**加注解后的返回结果：**
```json
{
    "name": "技术部",
    "manager1": {
        "name": "张三",
        "number": "EMP001"
    }
}
```

---

### 进阶使用

#### 场景一：Field 路径匹配 - 针对特定位置的对象

使用 `field` 属性可以精确控制特定路径下对象的序列化：

```java
@GetMapping("/department/detail")
@JsonViewExt(
    include = {
        @JsonFilterExt(clazz = Department.class, props = {"manager1", "managerList1"}),
        // 针对 managerList1 中的 Employee，只返回 name 和 address1
        @JsonFilterExt(clazz = Employee.class, field = "managerList1", props = {"name", "address1"}),
        // 针对其他位置的 Employee（如 manager1），返回 name 和 number
        @JsonFilterExt(clazz = Employee.class, props = {"name", "number"}),
        // 针对 managerList1.address1 路径下的 Address，只返回 province
        @JsonFilterExt(clazz = Address.class, field = "managerList1.address1", props = {"province"}),
        // 其他位置的 Address，返回 province 和 city
        @JsonFilterExt(clazz = Address.class, props = {"province", "city"})
    }
)
public Department getDepartmentDetail() {
    return departmentService.findById("1");
}
```

**返回结果说明：**
- `manager1`（Employee 类型）：返回 `name`、`number`
- `managerList1` 中的 Employee：返回 `name`、`address1`
- `managerList1[*].address1`：只返回 `province`
- 其他 Address：返回 `province`、`city`

---

#### 场景二：敏感数据脱敏

使用 `sensitives` 属性对敏感字段进行脱敏处理：

```java
@GetMapping("/employee/info")
@JsonViewExt(
    include = {
        @JsonFilterExt(
            clazz = Employee.class,
            props = {"name", "number", "address1"},
            sensitives = {
                @Sensitive(type = IdCardType.class, props = {"number"})
            }
        )
    }
)
public Employee getEmployeeInfo() {
    return employeeService.findById("1");
}
```

**不加注解时的返回结果：**
```json
{
    "id": "emp-001",
    "name": "张三",
    "number": "440301199001011234",
    "address1": {
        "id": "addr-001",
        "province": "广东省",
        "city": "深圳市"
    },
    "addressList1": []
}
```

**加注解后的返回结果：**
```json
{
    "name": "张三",
    "number": "440301********1234",
    "address1": {
        "id": "addr-001",
        "province": "广东省",
        "city": "深圳市"
    }
}
```

---

#### 场景三：同类型不同路径的差异化脱敏

```java
@GetMapping("/department/sensitive")
@JsonViewExt(
    include = {
        @JsonFilterExt(clazz = Department.class, props = {"manager1", "managerList1"}),
        // managerList1 中的员工编号需要脱敏
        @JsonFilterExt(
            clazz = Employee.class,
            field = "managerList1",
            props = {"name", "number"},
            sensitives = {@Sensitive(type = IdCardType.class, props = {"number"})}
        ),
        // manager1 的员工编号不需要脱敏
        @JsonFilterExt(clazz = Employee.class, props = {"name", "number"})
    }
)
public Department getDepartmentSensitive() {
    return departmentService.findById("1");
}
```

**返回结果：**
```json
{
    "manager1": {
        "name": "张三",
        "number": "440301199001011234"
    },
    "managerList1": [
        {
            "name": "李四",
            "number": "440301********5678"
        },
        {
            "name": "王五",
            "number": "440301********9012"
        }
    ]
}
```

---

#### 场景四：完整的复杂示例

```java
@GetMapping("/findById")
@JsonViewExt(
    include = {
        @JsonFilterExt(clazz = Department.class, props = {"manager1", "managerList1"}),
        @JsonFilterExt(
            clazz = Employee.class,
            field = "managerList1",
            props = {"name", "number", "address1"},
            sensitives = {@Sensitive(type = IdCardType.class, props = {"number"})}
        ),
        @JsonFilterExt(clazz = Employee.class, props = {"number", "address1", "addressList1"}),
        @JsonFilterExt(clazz = Address.class, field = "managerList1.address1", props = {"id", "province"})
    },
    exclude = {
        @JsonFilterExt(clazz = Address.class, props = {"id"})
    }
)
public Department findById(@RequestParam("id") String id) {
    return departmentService.findById(id);
}
```

**不加注解时的返回结果：**
```json
{
    "name": "name",
    "manager1": {
        "id": "9ccb342f-18f2-46c1-872e-892c0c3f6d14",
        "name": "name1",
        "number": "123456789012345678",
        "address1": {
            "id": "a7ab2748-68ec-4e52-97c3-a241317ef347",
            "province": "province1",
            "city": "city1"
        },
        "addressList1": [
            {"id": "c3b735e8-c8c7-4aab-b8e0-fddab5bcbe2e", "province": "province2", "city": "city2"},
            {"id": "e8f30e87-53b4-47f8-ba5a-1b49b6bca3a2", "province": "province3", "city": "city3"}
        ]
    },
    "managerList1": [
        {
            "id": "19cdb3dc-3877-4a45-a296-c71fae143040",
            "name": "name2",
            "number": "123456789012345679",
            "address1": {
                "id": "d724f421-a78a-494e-87d1-5f2e1622e3ba",
                "province": "province4",
                "city": "city4"
            },
            "addressList1": [
                {"id": "f9d53824-9f98-4eec-ba84-708113c804d8", "province": "province5", "city": "city5"},
                {"id": "22f0997d-9bc5-4415-bdd1-63d302c629ce", "province": "province6", "city": "city6"}
            ]
        },
        {
            "id": "e9e99b53-14cf-42d6-a70b-1595517a6671",
            "name": "name3",
            "number": "123456789012345670",
            "address1": {
                "id": "8fe894dd-d61d-4821-ae13-5bddd54839dc",
                "province": "province7",
                "city": "city7"
            },
            "addressList1": [
                {"id": "4dbb776a-22d0-4222-9c0b-83d7391a3eec", "province": "province8", "city": "city8"},
                {"id": "83c545ce-c7fd-4339-8c62-4ae3ceda32ec", "province": "province9", "city": "city9"}
            ]
        }
    ]
}
```

**加注解后的返回结果：**
```json
{
    "manager1": {
        "number": "123456789012345678",
        "address1": {
            "province": "province1",
            "city": "city1"
        },
        "addressList1": [
            {"province": "province2", "city": "city2"},
            {"province": "province3", "city": "city3"}
        ]
    },
    "managerList1": [
        {
            "name": "name2",
            "number": "123456********5679",
            "address1": {
                "id": "d724f421-a78a-494e-87d1-5f2e1622e3ba",
                "province": "province4"
            }
        },
        {
            "name": "name3",
            "number": "123456********5670",
            "address1": {
                "id": "8fe894dd-d61d-4821-ae13-5bddd54839dc",
                "province": "province7"
            }
        }
    ]
}
```

---

### 自定义脱敏处理器

项目内置了常用的脱敏处理器，如 `IdCardType`（身份证）、`PhoneType`（手机号）等。如需自定义脱敏规则，只需实现 `SensitiveType` 接口：

```java
/**
 * 自定义银行卡号脱敏处理器
 */
public class BankCardType implements SensitiveType {

    @Override
    public String desensitize(String value) {
        if (value == null || value.length() < 8) {
            return value;
        }
        // 保留前4位和后4位，中间用*替换
        return value.substring(0, 4)
            + "****"
            + "****"
            + "****"
            + value.substring(value.length() - 4);
    }
}
```

使用自定义脱敏处理器：

```java
@JsonViewExt(
    include = {
        @JsonFilterExt(
            clazz = Account.class,
            props = {"bankCard", "balance"},
            sensitives = {@Sensitive(type = BankCardType.class, props = {"bankCard"})}
        )
    }
)
```

---

### 字段过滤规则模板（Filter Rule Template）

#### 功能介绍

在实际开发中，往往存在多个接口需要复用相同字段过滤规则的场景。若通过复制粘贴的方式在每个方法上重复声明 `@JsonViewExt`，会带来以下问题：

- **代码冗余**：相同的过滤规则在多处重复出现
- **可读性降低**：注解内容过长，掩盖方法本身的业务含义
- **可维护性降低**：一旦规则需要修改，需要逐处查找并同步更新，容易遗漏

为此，框架提供了**字段过滤规则模板**功能，支持将公共的字段过滤规则抽取为模板，在需要的地方直接引用，实现规则的集中管理与复用。

------

#### 未使用模板时（原始写法）

```java
@GetMapping("/findById")
@JsonViewExt(
    include = {
        @JsonFilterExt(clazz = Department.class, props = {"manager1", "managerList1"}),
        @JsonFilterExt(
            clazz = Employee.class,
            field = "managerList1",
            props = {"name", "number", "address1"},
            sensitives = {@Sensitive(type = IdCardType.class, props = {"number"})}
        ),
        @JsonFilterExt(clazz = Employee.class, props = {"number", "address1", "addressList1"}),
        @JsonFilterExt(clazz = Address.class, field = "managerList1.address1", props = {"id", "province"})
    },
    exclude = {
        @JsonFilterExt(clazz = Address.class, props = {"id"})
    }
)
public Department findById(@RequestParam("id") String id) {
    return departmentService.findById(id);
}
```

当多个接口需要相同规则时，上述注解必须重复书写，维护成本极高。

------

#### 方案一：自定义模板注解

通过创建一个**自定义注解**，在其上标注 `@JsonViewExt`，将字段过滤规则封装在该注解中。使用时，直接将自定义模板注解标注在目标方法上即可。

##### 第一步：定义模板注解

```java
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@JsonViewExt(
    include = {
        @JsonFilterExt(clazz = Department.class, props = {"manager1", "managerList1"}),
        @JsonFilterExt(
            clazz = Employee.class,
            field = "managerList1",
            props = {"name", "number", "address1"},
            sensitives = {@Sensitive(type = IdCardType.class, props = {"number"})}
        ),
        @JsonFilterExt(clazz = Employee.class, props = {"number", "address1", "addressList1"}),
        @JsonFilterExt(clazz = Address.class, field = "managerList1.address1", props = {"id", "province"})
    },
    exclude = {
        @JsonFilterExt(clazz = Address.class, props = {"id"})
    }
)
public @interface TemplateA {

}
```

##### 第二步：在接口方法上使用模板注解

```java
@GetMapping("/findById")
@TemplateA
public Department findById(@RequestParam("id") String id) {
    return departmentService.findById(id);
}

@GetMapping("/findByName")
@TemplateA
public Department findByName(@RequestParam("name") String name) {
    return departmentService.findByName(name);
}
```

> **优点**：使用方式简洁直观，与 Spring 原生组合注解风格保持一致，语义清晰。

------

#### 方案二：自定义模板类

通过创建一个**继承或者实现 `JsonViewExtTemplate` 接口的模板类或者模板接口**，在其上标注 `@JsonViewExt`，将字段过滤规则封装在其中。使用时，通过 `@JsonViewExt(template = TemplateA.class)` 的方式引用模板类。

##### 第一步：定义模板类

```java
@JsonViewExt(
    include = {
        @JsonFilterExt(clazz = Department.class, props = {"manager1", "managerList1"}),
        @JsonFilterExt(
            clazz = Employee.class,
            field = "managerList1",
            props = {"name", "number", "address1"},
            sensitives = {@Sensitive(type = IdCardType.class, props = {"number"})}
        ),
        @JsonFilterExt(clazz = Employee.class, props = {"number", "address1", "addressList1"}),
        @JsonFilterExt(clazz = Address.class, field = "managerList1.address1", props = {"id", "province"})
    },
    exclude = {
        @JsonFilterExt(clazz = Address.class, props = {"id"})
    }
)
public interface TemplateA extends JsonViewExtTemplate {

}
```

##### 第二步：在接口方法上引用模板类

```java
@GetMapping("/findById")
@JsonViewExt(template = TemplateA.class)
public Department findById(@RequestParam("id") String id) {
    return departmentService.findById(id);
}

@GetMapping("/findByName")
@JsonViewExt(template = TemplateA.class)
public Department findByName(@RequestParam("name") String name) {
    return departmentService.findByName(name);
}
```

> **优点**：模板以普通 Java 接口的形式存在，便于统一归类管理，可集中存放在专门的模板包（如 `template` 包）中，结构更清晰。

------

#### 三种写法等价说明

以下三种写法在功能上**完全等价**，开发者可根据团队规范和个人偏好自由选择：

| 写法                              | 说明                                         |
| --------------------------------- | -------------------------------------------- |
| 直接使用 `@JsonViewExt`           | 原始写法，适合规则仅使用一次的场景           |
| 方案一：自定义模板注解            | 适合偏好注解组合风格的团队，语义直观         |
| 方案二：自定义模板类（interface） | 适合需要集中管理大量模板规则的项目，结构清晰 |

------

#### 最佳实践建议

- 建议将模板注解或模板类统一放置在独立的包中（如 `com.example.template`），便于查找和维护。
- 模板命名应具有业务含义，清晰表达该过滤规则的适用场景（如 `DepartmentDetailTemplate`、`EmployeeBriefTemplate` 等）。
- 当过滤规则发生变更时，只需修改模板定义，所有引用该模板的接口将自动生效，无需逐一修改。

---

## 📋 规则说明

| 规则 | 说明 |
|------|------|
| include 优先级高于 exclude | 当同时配置 include 和 exclude 时，include 规则优先生效 |
| field 精确匹配优先 | 带 field 的规则优先于不带 field 的通用规则 |
| 后定义覆盖先定义 | 多个相同 clazz 和 field 的规则，后定义的生效 |
| 嵌套路径使用 `.` 分隔 | 如 `managerList1.address1` 表示 managerList1 下的 address1 属性 |

## 🧱 模块结构

```
json-view-ext-parent/
├── json-view-ext-core                  # 核心模块
├── json-view-ext-spring-boot-starter   # Spring Boot 1.x / Spring Boot 2.x 支持 （JDK 8+）
└── json-view-ext-spring-boot3-starter  # Spring Boot 3.x 支持 （JDK 17+）
```

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

## 📄 开源协议

本项目基于 [Apache License 2.0](LICENSE) 开源。

---