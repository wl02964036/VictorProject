# VictorProject

這是一個包含前後端分離架構的全端專案，前端使用 **Angular** 開發，後端以 **Java + Spring Boot + Gradle** 為基礎建構。專案也包含我自學 Angular 期間製作的小作品。

---

## ᴽᵉᵒ 專案結構

| Branch | 說明 |
|--------|------|
| `main` | 專案初始化 |
| `Angular-Gradle-Backend` | Java 後端專案，使用 Gradle 管理，整合 Spring Boot |
| `Angular-Gradle-Frontend` | Angular 前端專案，包含 UI 表單、API 呼叫、JWT 認證 |
| `Angular-Sample` | 自學 Angular 時製作的簡易功能作品，例如登入頁、表單驗證、Todo Lsit等 |

---

## 🛠 使用技術

### 🔹 Backend
- JavaSE 17
- Spring Boot
- Gradle
- JWT 認證
- CAPTCHA 驗證
- MSSQL 資料庫連線

### 🔹 Frontend
- Angular 14
- Angular CLI
- Bootstrap
- RxJS
- JWT Token 管理

---

## 🚀 如何執行專案

### 📦 後端 Backend 啟動方式

```bash
cd Backend/Rx.Digital/Rx.WebApi
./gradlew bootRun
```

後端服務會啟動在：`http://localhost:8080/`

---

### 💻 前端 Frontend 啟動方式

```bash
npm install
npm start
```

前端網頁會啟動在：`http://localhost:4200/`

> ⚠️ 如要整合部署，請使用 `ng build` 並將輸出放入後端的 `static/` 資料夾。

---

## 🤝 作者

- Victor (w02964036)
- Java / Angular 全端開發者
- 正穩正學習前端框架、提升全端整合能力

---

## 📝 License

本專案為學習用途使用，未設定授權條款。

