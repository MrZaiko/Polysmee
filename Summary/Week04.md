# Summary of week 04

## Léo

I improved the appointment creation by adding a settings button which leads to an activity to handle the settings.

My time estimate was accurate, however creating classes/decorators for private and offline appointments turned out to be useless before I started to do so, and given that one should not change tasks during a sprint I didn't add a new task to replace it.

Next time, I'll make sure to discuss more with the other project members about the tasks if it is possible that one of them is unnecessary.

## Sami

I improved the room activity by adding a way of removing members from the room and modifying settings such as the title of the room.

Unlike past week, my time estimation was perfect, and it took me exactly 8 hours to complete the user story.

Next time, I will try to link the messaging function added by Adrien to the already existing message UI and make the whole room activity looks better.


## Youssef (Scrum Master)

I added to the calendar activity the possibility to see an appointment's details, as well as the possibility of modifying its title and course.

This week, my time estimation (9h) was also lower than last week's seeing as I had to refactor a lot of code to take into account the callback approach.

Next time, I'll try to add the functionnality that lets a user join an appointment's room just by clicking on its description in the calendar.

## Mathis
This week I implemented the connection between the app and the database, which included reading documentation, writing code and debugging it. It also caused massive refactorings in our code due to the fact that we had not foreseen that every database call is handled by callbacks and not directly.
I clearly underestimated the time again, but I think I got it now and I will try to give a good estimate for the next week.
This week I will work on making our tests separate from our actual database, by running a local emulator

## Thomas
I fixed the bug in the appointmentReminderNotification test, the bug did not allow multiple tests to be run as it did not return to the state at the start of the test. In addition to this, I also fixed the bug which allowed non-logged in users to access the main activity by clicking on the reminder notification. Additionally, I implemented the notification setter and remover for when the logged-in user would add/delete/obtain a changed appointment, but the interfaces I used changed during the sprint and so the work I did could not be integrated. So I wrote a function that would allow me next week to easily implement this task with a new function in the user interface we need to develop.

My task did not go smoothly as the implementation and interface of the classes/interfaces I used changed during the sprint. This was a necessary change, but it really complicated my task. This made my estimation completely wrong. I also fixed the notification click bug much faster than my estimate, as everything went smoothly. 

Next time, I hope no major API changes will be needed.


## Adrien

I finished implementing the messages. 

My time estimate was much better than last week, I only worked a bit more than 8 hours.

Next week I will try to be more efficient on my task, and to use more defensive programming.

## Overall team:

Each of us managed to implement their user story, and the application is taking form more and more. Some of the app's basics have been linked to the database.
I believe that to implement the self-assigned tasks, the team is getting better in their time estimates. However, some extra time had to be taken by some to refactor their existing code to adapt to the new callback approach. 
Next week, I believe we'll continue this momentum and put all these independent parts together, and hopefully have a functioning message service by the Easter holidays.