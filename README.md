# ***THIS IS CURRENTLY A WIP***

# RaiQuotes API + RedBot Container

This is a project intended to create a more versatile API for saving and playing with quotes saved by server users.  
This is intended for use by some friends, but if this finds its way into the hands of others then cool.  
This project makes use of a nice Redbot Docker image found here: https://github.com/PhasecoreX/docker-red-discordbot

<h1>Running the project</h1>
To run this project, you'll need Docker Desktop installed.

If you're just here to see a bot boot up alongside the API:
You'll need to add the file ".env" in the folder "Redbot Raiquotes" with the following line:
>TOKEN =[your disord bot token]

Once that is done, navigate to "~/Redbot Raiquotes" in your command line and run:
>docker-compose up

Should see Docker pull up three containers: Redbot, db, and API. You can click on the containers to see their logs.

To stop the containers from running, run:
>docker-compose down

(I make a good habit of doing this whenever I am done with docker as I like clean slates - you can ignore this and docker will start the containers when next it is launched.)

# Migrating your cogs to the container

Once you have run the containers at least once, a few folders will have been generated. Of note to us "~/Redbot RaiQuotes/redbot". Inside this folder are the "cogs" and "core" folder redbot uses.  
To migrate your old redbot features, simply copy your own versions of the folders into the generated ones and relaunch the container.