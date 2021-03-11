# Summary of week 03 

## Thomas
I implemented a notification broadcast receiver such that we will only need to set the alarm from android system to
broadcast to the broadcast receiver, at the right time when we will add an appointment to the user. I then change the implementation to use resource file so that it respects good practices and it will be easy to implement user defined parameter to choose when 
the reminder should start. There is however for android API 29,30 no sound from the notification , but it works with api 24

My task do not when smoothly as there for compatibility there is a lot of things to do, and I still have compatibility 
issue, moreover the documentation was not good and I got lost on my way to make it work, I finally step in a YouTube tutorial... .
Moreover, because it was a notification I needed to use uiautomator for testing, I had some bugs to resolve  
with my workspace. I did not implement the delete notification, task and set the notification to appear at desired time as
I already work 10+ hour. However, for next week those tasks should go smoothly as when I did my task I prepared those tasks
and I have already done most of the research needed.

Next time, I will not underestimate the "learning curve" of new to me android functionality (i.e broadcast receiver, uiautomator),
and will probably try to make something work before trying to make it compatible with most android API. So that, I got the 
general sense of how to write my task and then adapt it so that it is well compatible
## Léo
I implemented the ability to create an appointment by making the necessary class and activity
My time estimate wasn't accurate, as even though I finished what I had to do, it took much longer than 8 hours. 
Next time, I'll need to choose a task/tasks which should take less time in order to avoid going over estimate.
## Mathis (Scrum Master)
I implemented the login functionality for users, using the firebase authentication. 
The work was longer than expected, as firebase has (in my opinion) a pretty bad documentation.
I might want to improve on predicting the time it takes for an item in the backlog.
## Adrien
## Sami
I implemented the room UI (Messages, room participants, room info). 
The work was longer than expected because the tabLayout of the room activity was very difficult to implement, the documentation beeing quite complex to understand.
Next time, I will try to not underestimate the time needed to understand specific part of the android API when choosing a user story to implement. 
## Youssef