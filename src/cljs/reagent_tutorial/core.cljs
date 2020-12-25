(ns reagent-tutorial.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [goog.events.KeyCodes :as keycodes]
   [goog.events :as gev])
  (:import [goog.events EventType KeyHandler]))

(def current-elem (r/atom 0))

(def max-val (* 10 10))

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

(defn forwards-button []
  [:div
   {:style {:display "inline"}}
   [:input {:type "button"
            :value ">>"
            :on-click move-forwards}]])

(defn backwards-button []
  [:div
   {:style {:display "inline"}}
   [:input {:type "button"
            :value "<<"
            :on-click move-backwards}]])

(defn reset-button []
  [:div
   {:style {:display "inline"}}
   [:input {:type "button"
            :value "Reset"
            :on-click reset-current-elem}]])

(defn slide-component [i inner-vec]
  [:div {:key i
         :style {:display (if (= @current-elem i) "" "none")}}
   inner-vec])

(defn slide-builder [slides]
  (map-indexed (fn [i slide]
                 [slide-component i slide])
               slides))

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

(defn div-builder [items]
   (doall
     (for [item items]
       [:h1 {:key item
             :style {:display (if (= @current-elem item) "" "none")}} item])))

(defn number-thing []
  [:h1 @current-elem])

(defn main-component []
  [:div
   (slide-builder my-slides)
   [:div
     [backwards-button]
     [forwards-button]
     [reset-button]]])

(def keycode-map
  {keycodes/LEFT  move-backwards
   keycodes/RIGHT move-forwards
   keycodes/H     move-backwards
   keycodes/L     move-forwards
   keycodes/SPACE move-forwards
   keycodes/ESC   reset-current-elem})

(comment
  (assoc {} :style "font-weight:bold;")
  keycodes/D
  (get keycode-map keycodes/ArrowRight)
  )

(defn on-keydown [e]
  (.preventDefault e)
  ; (js/console.log (.. e -keyCode))
  (when-let [f (get keycode-map (.. e -keyCode))]
    (f))
  )

(defn mount-root []
  (rdom/render [main-component] (.getElementById js/document "app")))

(defn init! []
  ; What
  (mount-root)
  (gev/listen js/document "keydown" on-keydown))
