# TentClient
A Java implementation of the [Tent](http://tent.io/) protocol, from a 
client-specific perspective. 

## What Works?
* App registration, authentication and authorization.
* Posting status types with `text` and or `location`.
* Posting photo types with one or more images and a caption.
* Pulling a basic feed of posts from an entities server.
* Followers / Following.
* Follow and unfollow.

## What's Up Next?
* Better filtering of post feed/stream, ie since_id, types, entity etc.
* Posting more types.

## AndroidManifest.xml?
Since I'm also working on an Android Tent App. this library is implemented as an
Android Library but should work fine as regular Java code. 