(ns menu.app
  (:require [reagent.dom :as rdom]
            [reagent.core :as r]))

(defn draw
  ([ctx url] (draw ctx url 0 0 "source-over"))
  ([ctx url composite]
   (draw ctx url 0 0 composite))
  ([ctx url x y composite]
   (let [img    (js/Image.)
         _      (set! (.-src img) url)
         _      (set! (.-onload img)
                      #(do (set! (.-globalCompositeOperation ctx) composite)
                           (.drawImage ctx img x y)))]))
  ([ctx url x y width height]
   (draw ctx url x y width height "source-over"))
  ([ctx url x y width height composite]
   (let [img    (js/Image.)
         _      (set! (.-src img) url)
         _      (set! (.-onload img)
                      #(do (set! (.-globalCompositeOperation ctx) composite)
                           (.drawImage ctx img x y width height)))])))

(defn draw-logo [ctx url]
  (let [img (js/Image.)
        _   (set! (.-src img) url)
        _   (set! (.-onload img)
                  #(do (.drawImage ctx img 0 0 500 500)
                       (.drawImage ctx img 3300 0 500 500)
                       (.drawImage ctx img 0 1650 500 500)
                       (.drawImage ctx img 3300 1650 500 500)))]))

(defn roundedRect [ctx x y width height radius color]
  (set! (.-fillStyle ctx) color)
  (.beginPath ctx)
  (.moveTo ctx x (+ y radius))
  (.arcTo ctx x (+ y height)  (+ x radius) (+ y height) radius)
  (.arcTo ctx (+ x width) (+ y height) (+ x width) (- (+ y height) radius) radius)
  (.arcTo ctx (+ x width) y (- (+ x width) radius) y radius)
  (.arcTo ctx x y x (+ y radius) radius)
  (.fill ctx))

(defn label [ctx x y width height text color]
  ;(roundedRect ctx x y width height 45 "#977F4790")
  ;(roundedRect ctx x y 500 (* 1.5 height) 45 "#977F4750")
  (set! (.-font ctx) "140px Brush Script MT")
  (set! (.-shadowColor ctx) "white")
  (set! (.-shadowOffsetX ctx) 2)
  (set! (.-shadowOffsetY ctx) 2)
  (set! (.-fillStyle ctx) color)
  (.fillText ctx text (+ x 30) (+ y 90))
  (set! (.-shadowOffsetX ctx) 0)
  (set! (.-shadowOffsetY ctx) 0)
  ctx)

(defn render-prices [ctx x y]
  (let [x1 x y1 (+ y 1000)
        x2 (+ x 480) y2 (+ y 1285)
        x3 (+ x 1000) y3 (+ y 1130)
        y4 (+ y 1410)
        h  120 w1 350 w2 350]
    (roundedRect ctx x (+ y 1000) 1350 530 45 "#95866Fe0")
    (label ctx x1 y1 w1 h "500mg" "green")
    (label ctx x2 y1 w2 h "1000mg" "green")
    (label ctx x3 y1 w2 h "1500mg" "green")
    (label ctx x1 y2 w1 h "500mg" "green")
    (label ctx x2 y2 w2 h "1000mg" "green")
    (label ctx x3 y2 w2 h "1500mg" "green")
    (label ctx x1 y3 w1 h " $25" "black")
    (label ctx x2 y3 w1 h " $38" "black")
    (label ctx x3 y3 w1 h " $65" "black")
    (label ctx x1 y4 w2 h "$22.50" "black")
    (label ctx x2 y4 w1 h " $35" "black")
    (label ctx x3 y4 w1 h " $58" "black")))

(defn cbd-oil-prices [ctx x y]
  (let [x1 x y1 (+ y 1000)
        x2 (+ x 480) y2 (+ y 1285)
        x3 (+ x 1000) y3 (+ y 1130)
        y4 (+ y 1410)
        h  120 w1 350 w2 350]
    (roundedRect ctx x (+ y 1000) 1350 530 45 "#95866Fe0")
    (label ctx x1 y1 w1 h "500mg" "green")
    (label ctx x2 y1 w2 h "1000mg" "green")
    (label ctx x3 y1 w2 h "1500mg" "green")
    (label ctx x1 y2 w1 h "500mg" "green")
    (label ctx x2 y2 w2 h "1000mg" "green")
    (label ctx x3 y2 w2 h "1500mg" "green")
    (label ctx x1 y3 w1 h "$27.50" "black")
    (label ctx x2 y3 w1 h " $45" "black")
    (label ctx x3 y3 w1 h " $70" "black")
    (label ctx x1 y4 w2 h " $25" "black")
    (label ctx x2 y4 w1 h " $40" "black")
    (label ctx x3 y4 w1 h " $65" "black")))

(defn cbd-lotion-prices [ctx x y]
  (let [x1 x y1 (+ y 1000)
        x2 (+ x 480) y2 (+ y 1285)
        x3 (+ x 1000) y3 (+ y 1130)
        y4 (+ y 1410)
        h  120 w1 350 w2 350]
    (roundedRect ctx 1300 (+ y 1000) 450 530 45 "#95866Fe0")
    (label ctx x2 y1 w2 h "100mL" "green")
    (label ctx x2 y2 w2 h "50mL" "green")
    (label ctx x2 y3 w1 h " $30" "black")
    (label ctx x2 y4 w1 h " $15" "black")))

(defn millis
  "Get the current time in milliseconds."
  []
  (.getTime (js/Date.)))

(defn render-frame []
  (let [canvas (.getElementById js/document "canvas")
        ctx    (.getContext canvas "2d")
        tick   (- (mod (millis) 10000) 1400)]
    #_(if (< tick 0)
        (draw ctx "img\\flower\\bg-flower.png")
        (draw ctx "img\\tinctures\\bg-tinctures.png"))
    (draw-logo ctx "img\\happy-hemp-trans.svg")
    (render-prices ctx tick 0)
    (draw ctx "img\\tinctures\\Broad-Spectrum-CBD-Oil-1000mg-1200x1200-removebg-preview.png")))

(defn lotion []
  (let [x 500
        canvas (.getElementById js/document "canvas")
        ctx    (.getContext canvas "2d")]
    (.clearRect ctx 0 0 3840 2160)
    (roundedRect ctx (+ x 450) 270 1070 700 45 "#977F47c0")
    (draw ctx "img\\topicals\\IMG_7520-removebg-preview-1-2-transformed.png"
          (+ x 400) 200 1200 800)
    (label ctx 920 150 1150 120 "Extra Strength CBD Lotion" "black")
  ;(draw ctx "img\\tinctures\\oil.jpg" (+ x 500) 200 1000 1150)
  ;(render-prices ctx 870 300)
  ;(label ctx (+ x 900) 990 200 110 "$40" "black")
    (cbd-lotion-prices ctx (+ x 360) -30)
  ;(draw ctx "img\\tinctures\\refills-trans.png" (+ x 500) 200 1000 1150)
  ;(draw ctx "img\\tinctures\\bg-tinctures.png")
    ))

(defn gradient [ctx x y width height color1 color2 x1 y1 x2 y2]
  (let [right (.createLinearGradient ctx x1 y1 x2 y2)]
    (.addColorStop right 0 color1)
    (.addColorStop right 1 color2)
    (set! (.-fillStyle ctx) right)
    (.fillRect ctx x y width height)))

(defn balm []
  (let [canvas (.getElementById js/document "canvas")
        ctx    (.getContext canvas "2d")]
    (.clearRect ctx 0 0 3840 2160)
    (gradient ctx 900 400 500 800 "#B4B4B2" "#B4B4B200" 1000 300 900 300)
    (gradient ctx 1400 400 500 800 "#B4B4B2" "#B4B4B200" 1800 300 1900 300)
    (gradient ctx 1000 300 800 100 "#B4B4B2" "#B4B4B200" 900 400 900 300)
    (gradient ctx 1000 1200 800 100 "#B4B4B2" "#B4B4B200" 900 1200 900 1300)
    (gradient ctx 900 300 100 100 "#B4B4B2" "#B4B4B200" 1000 400 950 350)     ;; top left
    (gradient ctx 900 1200 100 100 "#B4B4B2" "#B4B4B200" 1000 1200 950 1250)  ;; bottom left
    (gradient ctx 1800 300 100 100 "#B4B4B2" "#B4B4B200" 1800 400 1850 350)    ;; top right
    (gradient ctx 1800 1200 100 100 "#B4B4B2" "#B4B4B200" 1800 1200 1850 1250) ;; bottom right
    (draw ctx "img\\topicals\\HHF-Balms-ES-transformed.png"
          800 190 1200 1200)
    (label ctx 950 150 900 120 "No Mess CBD Balm" "black")
    (label ctx 1300 1350 200 110 "$30" "black")
;(draw ctx "img\\topicals\\bg-topicals.png")
    ))

(defn rad-grad [ctx x y width height color1 color2 x0 y0 r0 x1 y1 r1]
  (let [grad (.createRadialGradient ctx x0 y0 r0 x1 y1 r1)]
    (.addColorStop grad 0 color1)
    (.addColorStop grad 0.5 color2)
    (set! (.-fillStyle ctx) grad)
    (.fillRect ctx x y width height)))

#_(defn bg-grad [ctx x y w h]
  #_(gradient ctx (+ x 100) (+ y 100) (/ w 2) (- h 200) "#B4B4B2" "#B4B4B200" 
                (+ x 200) (- y 100) (+ x 100) (- y 100))
  (gradient ctx (+ x (/ w 2)) (+ y 100) (/ w 2) (- h 200) "#B4B4B2" "#B4B4B200" 
                (+ (/ w 2) x 400) (- y 100) (+ x (- w 100)) (- y 100))
  #_(gradient ctx (+ x 200) y (- w 400) 100 "#B4B4B2" "#B4B4B200" 
                (+ x 100) (+ y 100) (+ x 100) y)
  #_(gradient ctx (+ x 200) (+ (- h 100) y) x 100 "#B4B4B2" "#B4B4B200" 
                (+ x 100) (+ (- h 100) y) (+ x 100) (+ (- h 100) 500))
  #_(gradient ctx (+ x 100) y 100 100 "#B4B4B2" "#B4B4B200" 
                (+ x 200) (+ y 100) (+ x 150) (+ y 50))     ;; top left
  #_(gradient ctx (+ x 100) (+ (- h 100) y) 100 100 "#B4B4B2" "#B4B4B200" 
                (+ x 200) (+ (- h 100) y) (+ x 150) (+ (- h 50) y))  ;; bottom left
  #_(gradient ctx (+ x (- w 200)) y 100 100 "#B4B4B2" "#B4B4B200" 
                (+ x (- w 200)) (+ y 100) (+ x (- w 150)) (+ y 50))    ;; top right
  #_(gradient ctx (+ x (- w 200)) (+ (- h 100) y) 100 100 "#B4B4B2" "#B4B4B200" 
                (+ x (- w 200)) (+ (- h 100) y) (+ x (- w 150)) (+ (- h 100) y 50))) ;; bottom right

(defn bg-grad [ctx x y w h]
    (gradient ctx (+ x 100) (+ y 100) (/ w 2) (- h 200) "#B4B4B2" "#B4B4B200"
              (+ x 200) (- y 100) (+ x 100) (- y 100))
    (gradient ctx (+ x (/ w 2)) (+ y 100) (/ w 2) (- h 200) "#B4B4B2" "#B4B4B200"
              (+ w (- x 200)) (- y 100) (+ x (- w 100)) (- y 100))
    (gradient ctx (+ x 200) y (- w 400) 100 "#B4B4B2" "#B4B4B200"
              (+ x 100) (+ y 100) (+ x 100) y)
    (gradient ctx (+ x 200) (+ (- h 100) y) (- w 400) 100 "#B4B4B2" "#B4B4B200"
              (+ x 100) (+ (- h 100) y) (+ x 100) (+ (- h 100) y 100))
    (gradient ctx (+ x 100) y 100 100 "#B4B4B2" "#B4B4B200"
              (+ x 200) (+ y 100) (+ x 150) (+ y 50))     ;; top left
    (gradient ctx (+ x 100) (+ (- h 100) y) 100 100 "#B4B4B2" "#B4B4B200"
              (+ x 200) (+ (- h 100) y) (+ x 150) (+ (- h 50) y))  ;; bottom left
    (gradient ctx (+ x (- w 200)) y 100 100 "#B4B4B2" "#B4B4B200"
              (+ x (- w 200)) (+ y 100) (+ x (- w 150)) (+ y 50))    ;; top right
    (gradient ctx (+ x (- w 200)) (+ (- h 100) y) 100 100 "#B4B4B2" "#B4B4B200"
              (+ x (- w 200)) (+ (- h 100) y) (+ x (- w 150)) (+ (- h 100) y 50))) ;; bottom right

(let [x 800 y 400 w 1200 h 800
      canvas (.getElementById js/document "canvas")
      ctx    (.getContext canvas "2d")]
  (.clearRect ctx 0 0 3840 2160)
  ;(bg-grad ctx x y w h)
 ; (roundedRect ctx 1300 500 500 400 45 "#977F47c0")
  ;(bg-grad ctx 800 400 1000 600)
  
  
  ;(roundedRect ctx 800 400 1000 600 45 "#977F47c0")
  (draw ctx "img\\topicals\\body-oil-3-transformed.png" x y w h)
  (bg-grad ctx 800 400 w h)
  (label ctx (+ x 150) 150 900 120 "CBD Massage oil" "black")
  (bg-grad ctx 1130 1220 500 250)
  (label ctx (+ x 450) (+ y h 100) 200 110 "$40" "black")
  ;(rad-grad ctx 100 100 300 300 "#B4B4B2" "#B4B4B200" 220 200, 30, 220, 200, 70)
  ;(draw ctx "img\\topicals\\bg-topicals.png")
)

(defn app []
  [:div#app
   ;[render-frame]
   ])

(defn render []
  (rdom/render [app]
               (.getElementById js/document "root")))

(defn ^:dev/after-load start []
  (render)
  (js/console.log "start"))

(defn ^:export init []
  (js/console.log "init")
  (start))

(defn ^:dev/before-load stop []
  (js/console.log "stop"))