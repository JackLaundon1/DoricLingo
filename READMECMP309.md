# DoricLingo

DoricLingo is a Doric language learning app designed to help users learn Doric phrases with translations, pronunciations, and interactive progress tracking. 

## Features

- **Account Management**: 
  - Users can create and delete accounts.
  - Users can log in and log out.
  - Users can reset their passwords if they forget.
  
- **Training**: 
  - Users are presented with Doric sentences, translations, and audio pronunciations.
  - As users progress through the sentences, their progress is tracked and stored in a local Room database, then synced to Firebase. 
  - If no network connection is available, data is stored locally and uploaded when a connection is restored.
  
- **Quiz**: 
  - Users can test their learning through a quiz integrated into the app.

- **Location Feature**: 
  - Users can see their distance from Aberdeen city centre.
  
- **Wikipedia Integration**: 
  - A summary of Aberdeen City is displayed using the Wikipedia API and Retrofit.

## App Behavior

- **Orientation Handling**: 
  - Each screen properly handles orientation changes when required.
  
- **Input Validation**: 
  - Input is validated to ensure proper data entry.

- **Persistent Data**: 
  - Data persists during state changes to provide a seamless user experience.

---

**Note**: The API used for the location service has been removed before uploading to GitHub for privacy reasons.

## Links

- **Source Code**: [https://github.com/JackLaundon1/DoricLingo](https://github.com/JackLaundon1/DoricLingo)
- **Presentation**: [www.example.com](www.example.com)

---

**No specific instructions** when marking the app.
