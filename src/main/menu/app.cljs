(ns menu.app
  (:require [reagent.dom :as rdom]
            [reagent.core :as r]
            [goog.object :as o]))

(defonce !files (r/atom []))
(defonce !bg (r/atom nil))

(defn upload-images [e]
    (let [dom    (o/get e "target")
          files (o/get dom "files")]
      (reset! !files [])
      (doseq [file files]
        (let [reader (js/FileReader.)]
          (.readAsDataURL reader file)
          (set! (.-onload reader)
                #(swap! !files conj (-> % .-target .-result)))))))

(defn upload-bg [e]
  (let [dom    (o/get e "target")
        files (o/get dom "files")]
    (reset! !files [])
    (doseq [file files]
      (let [reader (js/FileReader.)]
        (.readAsDataURL reader file)
        (set! (.-onload reader)
              #(reset! !bg (-> % .-target .-result)))))))

(defn import-bg []
  [:div
   [:h1 "Select background"]
   [:input#bg
    {:type      "file"
     :on-change upload-bg}]])

(defn import-images []
  [:div
   [:h1 "Import images"]
   [:input#input
    {:type      "file"
     :multiple true
     :on-change upload-images}]])

(defn images [files]
  (into [:div.flex-container]
        (for [file files]
          [:img.flex-item {:src   file
                 :width 200 :height 200}])))


;(def url (first @!files))
;(def img (js/Image.))
;(set! (.-src img) url)
;(def canvas (.getElementById js/document "canvas"))
;(def ctx    (.getContext canvas "2d"))
;(set! (.-onload img) (.drawImage ctx img 0 0))

(defn original [] 
  (let [url (first @!files)
        img (js/Image.)
        _ (set! (.-src img) url)
        canvas (.getElementById js/document "canvas")
        ctx    (.getContext canvas "2d")
        _ (set! (.-onload img) (.drawImage ctx img 0 0))]
    (.drawImage ctx img 0 0)))

(defn transparent [r g b]
  (let [url (first @!files)
        img (js/Image.)
        _ (set! (.-src img) url)
        canvas (.getElementById js/document "canvas")
        ctx    (.getContext canvas "2d")
        _ (set! (.-onload img) (.drawImage ctx img 0 0))
        _  (.drawImage ctx img 0 0)
        imageData (.getImageData ctx 0 0 (.-width canvas) (.-height canvas))
        data (.-data imageData)]
    (doseq [i (filter #(and
                        (= r (aget data (- % 3)))
                        (= g (aget data (- % 2)))
                        (= b (aget data (- % 1))))
                      (range 3 (.-length data) 4))]
      (aset data i 0))
    (.putImageData ctx imageData 0 0)))

(let [url (first @!files)
      img (js/Image.)
      _ (set! (.-src img) url)
      canvas (.getElementById js/document "canvas")
      ctx    (.getContext canvas "2d")
      _ (set! (.-onload img) (.drawImage ctx img 0 0))
      _  (.drawImage ctx img 0 0)
      imageData (.getImageData ctx 0 0 (.-width canvas) (.-height canvas))
      data (.-data imageData)]
  (take 50 data))


(set! (.. js/document -body -style -backgroundImage) (str "url(" @!bg ")"))

(transparent 255 255 255)

(defn app []
   [:div#app
     {:style {:background-image (str "url(" @!bg ")")}}
    [import-bg]
    [import-images]
    [images @!files]])

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
