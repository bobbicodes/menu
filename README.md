# Happy Hemp Farmacy

![animated slide](happyhemp-hd.gif)

## Progress report Jan. 24

Investigated possible technologies to use and settled on HTML5 Canvas for the image manipulation and FFmpeg for video composition.

### Background images

Scraped the website for image assets and found a few that could be upscaled/tiled into nice looking [4k wallpapers](backgrounds).

### Objective

To create a menu system where an image sequence can be generated based on the current inventory, so it can be easily updated to display the correct products at the correct prices.

### Image presentation/manipulation - HTML5 Canvas

Canvas gives efficient programmatic access to the pixel data and leverages the browser's renderer.

### Video composition - FFmpeg

Each product page consists of 2 "layers", the background and the product/price table. FFmpeg is used to compose images into video.

## Idea: Hardware solution

Instead of displaying the images using browser technology which requires an entire computer, we could instead flash a microcontroller to display them. Besides eliminating the need for a dedicated computer/keyboard/mouse just for signage, it also has the advantage of booting up instantaneously since it no longer requires an operating system. This should then be followed by adding a system for updating the unit's firmware when you want to change the prices or reflect stock updates.

A quick search for UHD 4k capable boards with HDMI found me this for the modest price of $13.50: https://www.aliexpress.us/item/3256802965790134.html

## Development

You'll likely want to change the name from `shadow-reagent` to whatever your project is called. Here's where you need to do that:

1. In the `shadow-cljs.edn` file in the project root (so your `init` fn will be called)
2. Rename the directory in the `src` path (inside the project root)- *make sure to change hyphens (-) to underscores (_).*
3. In the `ns` macro at the top of `app.cljs`

Now you can do the thing:

```bash
$ npm install
added 97 packages from 106 contributors in 5.984s
```

Start the development process by running:

```bash
$ npx shadow-cljs watch app
...
[:app] Build completed. (134 files, 35 compiled, 0 warnings, 5.80s)
```

Or simply `jack-in` from your editor. Your app will be served at: at [http://localhost:8080](http://localhost:8080).

## Production build

```bash
npx shadow-cljs release app
```
