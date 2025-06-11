# 📝 NoteApp – Basit Not ve Klasör Yönetimi

Android için geliştirilmiş sade ve işlevsel bir not uygulamasıdır.  
Kullanıcılar not ekleyebilir, düzenleyebilir, silebilir ve klasörler oluşturup notlarını organize edebilir.

## 🚀 Özellikler

- 📌 Not ekleme, düzenleme, silme
- 📁 Klasör oluşturma ve notları taşıma
- 🖼 Görsel destekli notlar (galeriden seçim + kırpma)
- 🧩 ViewBinding ve modern Android mimarisi
- 🌙 Karanlık tema uyumu
- 💾 SharedPreferences ile veri kalıcılığı
- ♻️ RecyclerView + GridLayout ile dinamik görünüm
- 📦 `Parcelable` model yapısı ile hızlı veri aktarımı

## 🖼 Ekran Görüntüleri



## 🔧 Kullanılan Teknolojiler

- Kotlin
- Android SDK
- ViewBinding
- UCrop
- Gson
- RecyclerView
- SharedPreferences

## 📂 Proje Yapısı

```bash
├── MainActivity.kt
├── AddNoteActivity.kt
├── CreateFolderActivity.kt
├── ChooseFolderActivity.kt
├── FolderDetailActivity.kt
├── NotesAdapter.kt
├── Prefs.kt
├── model/
│   ├── Note.kt
│   └── Folder.kt
├── res/layout/
│   ├── activity_main.xml
│   ├── activity_add_note.xml
│   ├── activity_create_folder.xml
│   ├── activity_choose_folder.xml
│   ├── activity_folder_detail.xml
│   ├── item_note.xml
│   └── item_folder.xml
