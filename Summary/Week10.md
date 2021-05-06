# Summary of week 10

## Sami
This week, I made the ediPictureActivity and voiceCall UI better and added a color picker in the editPictureActivity.
I managed to stay mostly within the 8 hour time bound.
Next week, I will refactor how the database listeners are handled since they create a lot of errors in the tests.

## Youssef
This week I implemented the friend system so users can add/remove friends, and add them to an appointment more easily.
The time approximation of 8 hours was correct.
Next week I'll refactor the user profile so it's more user-friendly, and add support for profile picture.

## Léo
I added support for french language. This took slightly less time than expected but as I didn't know exactly how this task had to be done, I expected to either under or over estimate the time. This was good however because it allowed me to fix some issues pointed out in the code review.
My time estimate was close to accurate.
Next time, I'll keep trying to estimate the time correctly.

## Adrien
I implemented the voice changer back-end, the turn off other's volume feature and I wrote code for the participants to appear at the top of the room if they are currently in call.
My time estimate was good and the work organisation with Thomas was perfect.
Next time I'll keep working this way as everything went well.

## Thomas
This sprint I implemented the front end of the voice tuner settings, i.e. choose which voice tune to apply.
My time estimate was correct as I work 8 hours.
Next time, I will try to have a good estimate again and continue my path to mostly get good time estimate.

# Mathis (SM)
This sprint, I implemented a way for the app to work offline, i.e. it now is able to store appointments, settings and messages to disk.
While my work was supposed to be only investigation, I quickly discovered that firebase handles this on its own, there is just a one-liner setting to achieve this.
I then worked to refactor the app in order to apply the recommendations of the code review, but I found out that Firebase has a very specific way to handle settings strings and lost hours to debug this, only to realise the code was much cleaner beforehand. I then dropped these modifications, which still frustrates me for the amount of work I have put in.
I will try to be more useful to the codebase during the next weeks, as this was not up to the standard I wish to promote.
Next time, I will work on the GUI for welcoming new users, assuming the destabilization sprint does not significantly impact the trajectory the app is taking.