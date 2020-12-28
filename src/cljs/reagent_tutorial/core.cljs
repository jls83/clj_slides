(ns reagent-tutorial.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [goog.events.KeyCodes :as keycodes]
   [goog.events :as gev])
  (:import [goog.events EventType KeyHandler]))

(def my-slides
  [
   [:h1 "Hi there"]
   [:div
    [:h2 "My first slide"]
    [:p "This is my first slide"]]
   [:div
    [:h2 "A second slide"]
    [:p "This is my second slide"]]
   ])

(def max-val (count my-slides))

; Movement functions & state
(def current-elem (r/atom 0))

(defn move-forwards []
  (swap! current-elem (fn [v]
                       (if (= v (dec max-val))
                         v
                         (inc v)))))

(defn move-backwards []
  (swap! current-elem (fn [v]
                       (if (= v 0)
                         v
                         (dec v)))))

(defn reset-current-elem []
  (reset! current-elem 0))

; Slide controls
(defn forwards-button []
  [:input {:type "button"
           :value ">>"
           :style {:display "inline"}
           :on-click move-forwards}])

(defn backwards-button []
  [:input {:type "button"
           :value "<<"
           :style {:display "inline"}
           :on-click move-backwards}])

(defn reset-button []
  [:input {:type "button"
           :value "Reset"
           :style {:display "inline"}
           :on-click reset-current-elem}])

(defn controls []
  [:div {:id "controls-container"}
   [backwards-button]
   [forwards-button]
   [reset-button]])


; Slide components & "builders"
(defn slide-component [i inner-vec]
  [:div {:key i
         :style {:display (if (= @current-elem i) "" "none")}}
   inner-vec])

(defn slide-builder [slides]
  (->> slides
    (map-indexed (fn [i slide] [slide-component i slide]))))

(defn main-component []
  [:div
   [:div {:id "slides-container"}
     (slide-builder my-slides)]
   [controls]])

(def keycode-map
  {keycodes/LEFT  move-backwards
   keycodes/RIGHT move-forwards
   keycodes/H     move-backwards
   keycodes/L     move-forwards
   keycodes/SPACE move-forwards
   keycodes/ESC   reset-current-elem})

(defn on-keydown [e]
  (.preventDefault e)
  (when-let [f (get keycode-map (.. e -keyCode))]
    (f)))

(defn mount-root []
  (rdom/render [main-component] (.getElementById js/document "app")))

(defn init! []
  (mount-root)
  (gev/listen js/document "keydown" on-keydown))

(comment
  )
