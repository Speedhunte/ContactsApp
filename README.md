# Contacts App (Вариант 3)

Android-приложение для управления контактами устройства с функцией поиска и удаления полностью идентичных дубликатов через изолированный фоновый процесс.


* **UI-слой:** Jetpack Compose (Material 3) — для построения декларативного и реактивного интерфейса.
* **Архитектурный паттерн:** MVVM (Model-View-ViewModel) с четким разделением на слои (Presentation, Domain, Data) по принципам Clean Architecture.
* **Асинхронность:** Kotlin Coroutines & Flow (StateFlow / SharedFlow) — для управления состояниями экрана, обработки UI-событий.
* **Внедрение зависимостей:** Dagger Hilt — для инжекции зависимостей (Dependency Injection) 
* **Загрузка изображений:** Coil — для эффективного и асинхронного кэширования и отображения аватаров/фотографий контактов.

## 📸 UI приложения

<p align="center">
  <img src="https://github.com/user-attachments/assets/8cf7c8b9-9780-43b0-8da1-034bad5087c4" width="28%" alt="Список контактов" />
  <img src="https://github.com/user-attachments/assets/4da08182-0688-4ddd-a974-69ce402ed81c" width="28%" alt="Результат удаления" />
</p>
<p align="center">
  <sub><i>Экран со списком контактов и уведомление об успешном удалении дубликатов через AIDL сервис</i></sub>
</p>
