[![Build Status](https://img.shields.io/cirrus/github/MrZaiko/Polysmee)](https://cirrus-ci.com/github/MrZaiko/Polysmee)
![Build Status](https://img.shields.io/codeclimate/coverage/MrZaiko/Polysmee)
# Polysmee
Polysmee is an Android application that lets you discover new student communities, and communicate with your friends in one single unified application.
It was built for the EPFL CS-306 "Software Development Project" course.

See us on [Youtube](https://www.youtube.com/watch?v=HE_2EAxEcnk)!

## What it does

Polysmee lets you create and join appointments, which may be public (anybody can join) or private (the owner decides who joins).

These appointments enable users to chat and communicate through audio and/or video.

Polysmee supports, among other things, picture sharing, notifications for appointments and synchronization of your appointments to calendar apps such as Google Calendar.

## Requirements

### Split app model

This application uses two main cloud service providers : [Google Firebase](https://firebase.google.com) and [Agora](https://www.agora.io). 

### Sensor usage

Camera : Video call, take picture and edit them
Microphone : Voice call and voice effects

### User support

Every Polysmee user must have a valid google account.

### Local cache

All pictures are saved in local storage. Firebase also offers working cache implementation (mainly used for messages and appointments).

### Offline mode

All cached information are displayed even in offline mode (BUG: Note that, in rare occasions, the cache is temporarily unavailable (an error is shown) and you should restart the app). Every action needing an actual connection such as creating an appointment is delayed.

## References

[Icons](https://fonts.google.com/icons?selected=Material+Icons)

[Circle Image View](https://github.com/hdodenhof/CircleImageView)

[Color Picker](https://github.com/duanhong169/ColorPicker)

[Image Cropper](https://github.com/ArthurHub/Android-Image-Cropper/)

## Authors (alphabetical order)

@AttiaYoussef

@Fermeli

@Morpheo1

@MRandl

@MrZaiko

@tombenamato
