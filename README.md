# 📝 NoteApp – Notlarınızı Düzenleyin / Manage Your Notes Easily

**TR:** Basit ve işlevsel bir Android not uygulaması.  
**EN:** A simple yet powerful Android note-taking application.

---

## 🚀 Özellikler / Features

- 📌 Not ekleme, silme, düzenleme  
  📌 Add, delete, and edit notes
- 📁 Klasör oluşturma, not taşıma  
  📁 Create folders and move notes between them
- 🖼 Görsel destekli notlar (galeri + kırpma)  
  🖼 Image support with cropping
- 🌓 Karanlık ve aydınlık tema uyumu  
  🌓 Dark & light theme support
- 💾 SharedPreferences ile veri saklama  
  💾 Data stored via SharedPreferences
- 🔄 ViewBinding, Parcelable model yapısı  
  🔄 ViewBinding & Parcelable models

---


## 🧱 Kullanılan Teknolojiler / Built With

- Kotlin
- Android SDK
- ViewBinding
- UCrop (for image cropping)
- Gson (for JSON serialization)
- RecyclerView + GridLayout
- SharedPreferences

---

🖼 Ekran Görüntüleri / Screenshots
🏠 Ana Sayfa / Home Screen
<img src="home.png" alt="Home Screen" width="300"/>
📁 Klasörler / Folders
<img src="folder.png" alt="Folder Screen" width="300"/>
📝 Not Ekleme / Add Note
<img src="note.png" alt="Note Screen" width="300"/>
---

## 🛠️ Proje Yapısı / Project Structure

```bash
├── MainActivity.kt
├── AddNoteActivity.kt
├── CreateFolderActivity.kt
├── ChooseFolderActivity.kt
├── FolderDetailActivity.kt
├── NotesAdapter.kt
├── Prefs.kt
├── res/layout/
│   ├── activity_main.xml
│   ├── activity_add_note.xml
│   ├── item_note.xml
│   └── ...
