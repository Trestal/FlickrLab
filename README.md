[![Build Status](https://travis-ci.org/Trestal/FlickrLab.svg?branch=master)](https://travis-ci.org/Trestal/FlickrLab)

# FlickrLab

Flickr view is an image viewer that downloads images from the Flickr API and presents them nicely with a ViewPager and a RecyclerView. It is targetted for devices with Android 5.0 and upwards. Support libraries are used so it may run on lower versions but it was not tested.

The ViewPager and RecyclerView stay in sync while scrolling. Any time a new image is selected, the ViewPager updates and sets the correct position on the RecyclerView.

The loading dialog may be interrupted at any time to view the images that had been downloaded up to that point.

## Recent Changes 31/05/16
1) Structural improvements to both the loading fragment and the loaders themselves. This includes creating an abstract parent (Loader) for the ImageLoader and JsonLoader classes to manage shared code.
2) MultiThreading has been added so that multiple images can be loaded at once. The number of threads is derived from the number of available processors the loading device has. Images load much faster now. 
3) Visual improvement to the thumbnails. Thumbnails now appear to be equal sized and cropped.
4) Better error handling such as more meaningful messages in the event of errors. Connection timeout has also been added to the image loaders so that the process doesn't indefinitely hang with a bad connection.
5) Highlight effect is now more pronounced

## Approach 1

My initial approach tried loading the images while the user could interact with the app. I figured that this would allow te user to instantly use the app rather than wait for loading. This presented a number of issues an challenges. With my high internet speed at home, the delay in images loading was neglible but when trying it on mobile data or other Wi-Fi access points, the user was stuck seeing a blank page until the image loaded.

It also presented challenges with state restoration. Bitmaps were stored in memory and therefore needed to persist between configuration changes. It also placed 70 - 80% of the application logic in the FragMain class as it handled AsyncTasks, Fragment transactions and all view interaction logic.

Ultimately I discarded this project and started from scratch since too many work arounds were required just to make things work. It was unreliable and difficult to read.

## Approach 2

My second approach, and the one I stuck with, separated out the main logic between ActMain and FragMain. It also presents the user with a ProgressDialogue and gives feedback as to how far along the image loading is. The user can cancel loading at any time and view what has currently loaded.

Essentially all GUI interaction (with the exception of the Toolbar) is handled in FragMain. ActMain handles the creation of UIless fragments and callbacks. Once the final callback is received (when the loading queue reaches size 0), the UI fragment (FragMain) is built and added to the container.

Instead of storing the images in memory, they are now saved to a temporary folder on disk. Each image is typically no more than a couple hundred kb so this doesn't take much space and loads quickly. It also means that handling orientation changes is much simpler. Nothing needs to be bundled up and passed to the next instance when the Fragment/Activity is reinitialised.

## Improvements to come
1) Improved ImageView. Add the ability to zoom and pan on the selected image.

2) Add nicer graphic effects. Adding something like a blur effect would be nice
