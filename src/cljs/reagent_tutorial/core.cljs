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


(defn add-attrs-map [hiccup-vec]
  (if (= (count hiccup-vec) 2)
   [(first hiccup-vec) {} (last hiccup-vec)]
   hiccup-vec))

(defn set-list-element-bold [list-elem]
  (let [[tag attrs value] (add-attrs-map list-elem)]
    (if (= @current-elem value)
      [tag (assoc attrs :style {:font-weight "bold"}) value]
      [tag attrs value])))

(defn list-elem-builder [items]
  (doall
    (for [item items]
      (set-list-element-bold [:li {:key item} item]))))


(defn outer-list []
  [:ul
    (list-elem-builder (range max-val))])


(defn main-component-old []
  [:div
    [outer-list]
    [:div
     [backwards-button]
     [forwards-button]
     [reset-button]]])

(defn div-builder [items]
   (doall
     (for [item items]
       [:h1 {:key item
             :style {:display (if (= @current-elem item) "" "none")}} item])))

(defn number-thing []
  [:h1 @current-elem])

(defn main-component []
  [:div
   (div-builder (range max-val))
   [:div
     [backwards-button]
     [forwards-button]
     [reset-button]]])

(def keycode-map
  {keycodes/LEFT  move-backwards
   keycodes/RIGHT move-forwards
   keycodes/H     move-backwards
   keycodes/L     move-forwards
   keycodes/ESC   reset-current-elem})

(comment
  (assoc {} :style "font-weight:bold;")
  (list-elem-builder (range 10))
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
