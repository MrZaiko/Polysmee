# Summary of week 04

## Léo

I completely connected the appointment creation to the database. Now the appointment will appear on the database will everything set up correctly

My time estimate was accurate, however one small thing i planned to do turned out to not be a "need to have" so i didn't do it, so I may have been over estimate if i did.

Next time, I'll keep trying to extimate the time correctly

## Sami (Scrum Master)

Like past week, I improved the room activity by restraining the modification of the room to the owner only. I also linked the send, edit and delete messages functionnality 
developped by Adrien to the UI. We finally have a fully functionnal room.

Again, my time estimation was accurate but I did not take into account the time needed to do the demo, so I ended up working 10 hours instead of 8.

Next time, I will try to improve the layout of each activity to make the whole app looks better.

## Youssef

I added to the calendar activity the possibility to click on a created appointment to join its room, as well as managing the users from the appointment's details

This week, my time estimation was on point; I did my 8 hours almost exactly.

Next week, I'll try to implement the possibility for the user to select any date they want, and let them see that day's appointment.

## Mathis

This week, I implemented a way to call the firebase emulator while running on Cirrus, so that the tests stop thrashing our production database with garbage.
It enables tests to create fake users and appointments and answers queries, while deleting all data at the end of the cirrus run.

I managed to stay mostly within the 8 hour time bound.

Next week, I will work on a new database implementation that enables group owners to ban users from a group.

## Thomas
This week I implemented a user interface to change the time at which a user will be reminded, it took me much more time than predicted as it was a special case of android. The testing of the implementation was not easy.

I didn't manage to do all the tasks that I wanted to do, I only did the implementation of a setting interface.

Next week, I will work on the tasks that I did not implemented this week.


## Adrien

This week I first refactored the database reader of the room to make it more efficient, then I implemented the back-end part of the edit/delete message feature.

My time estimate was fine.

Next week I'll try not to waste time in merging conflict so that I can focus more on the code.

## Overall team:

Thanks to the hardwork of everyone, we are finally getting really close to have all the core features of our app. 

I feel like the team is trying to improve the communication issues we encountered so assembling the work of everyone for everyone is easier.

Next time, I hope we will finally add the remaining blocks (mainly notifications) and improve the global look and feel of the app.
