[![Build Status](https://travis-ci.org/Trestal/FlickrLab.svg?branch=master)](https://travis-ci.org/Trestal/FlickrLab)

# FlickrLab

Flickr view is an image viewer that downloads images from the Flickr API and presents them nicely with a ViewPager and a RecyclerView. It is targetted for devices with Android 5.0 and upwards. Support libraries are used so it may run on lower versions but it was not tested.

The ViewPager and RecyclerView stay in sync while scrolling. Any time a new image is selected, the ViewPager updates and sets the correct position on the RecyclerView.

## Approach 1

My initial approach tried loading the images while the user could interact with the app. I figured that this would allow te user to instantly use the app rather than wait for loading. This presented a number of issues an challenges. With my high internet speed at home, the delay in images loading was neglible but when trying it on mobile data or other Wi-Fi access points, the user was stuck seeing a blank page until the image loaded.

It also presented challenges with state restoration. Bitmaps were stored in memory and therefore needed to persist between configuration changes. It also placed 70 - 80% of the application logic in the FragMain class as it handled AsyncTasks, Fragment transactions and all view interaction logic.

Ultimately I discarded this project and started from scratch since too many work arounds were required just to make things work. It was unreliable and difficult to read.

## Approach 2

My second approach, and the one I stuck with, separated out the main logic between ActMain and FragMain. It also presents the user with a ProgressDialogue and gives feedback as to how far along the image loading is. The user can cancel loading at any time and view what has currently loaded.

Essentially all GUI interaction (with the exception of the Toolbar) is handled in FragMain. ActMain handles the creation of UIless fragments and callbacks. Once the final callback is received (when the loading queue reaches size 0), the UI fragment (FragMain) is built and added to the container.

Instead of storing the images in memory, they are now saved to a temporary folder on disk. Each image is typically no more than a couple hundred kb so this doesn't take much space and loads quickly. It also means that handling orientation changes is much simpler. Nothing needs to be bundled up and passed to the next instance when the Fragment/Activity is reinitialised.

## Improvements
1) Parallel loading of images. Currently a UIless fragment that retains its instance is created and this runs an AsyncTask. Each time it finishes, the fragment is destroyed and a new one made since the same AsyncTask cannot have execute called again. This works fine but means we can only load 1 url at a time. So a large improvement would be to send the list of urls to the fragment and use THREAD_POOL_EXECUTOR.

2) Error handling. I didn't add a whole lot of error handling in.
For instance if the Flickr API is down then the JSON AsyncTask will fail. But this won't notify the user or do anything meaningful. An improvement would be to wrap the JSONObject that the AsyncTask is returning along with a useful message. The callback would then display a message to the user if there was a problem.

JSON parsing also has no error handling. There is an assumption that Flickr will provide the app with perfectly formatted JSON. Probably true most of the time but it will just cause an error and loading no images if this does happen.

3) Improved ImageView. Add the ability to zoom and pan on the selected image.

4) Add nicer graphic effects. Currently the selected item is simply surrounded by a red border. Adding something like a blur effect so this is not so hard would be nice

5) Add more unit tests. Admittedly this is something I need to learn more of. I will continue to add more unit tests even after the submission of this project simply so I can learn more about them.

6) Structure. There are a few structural changes that, while not necessary, are good practice. For instance the classes JsonLoader and ImageLoader both use much of the same methods. This could be added to an abstract class that each one extends
