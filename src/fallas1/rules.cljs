(ns fallas1.rules
  (:require [clara.rules
             :refer [insert!]
             :refer-macros [defsession defrule defquery]]))


;; Intensidad: Leve, Moderada, Alta


(defrecord Intensity [intensity])
;; Localización: Pecho, Cadera, Zona lumbar, Brazo, Zona posterior del cuello, Miembro superior, Cabeza
(defrecord Location [location])
;; Irradiació: No irradia, Irradia hacia otra zona
(defrecord Irradiation [])
;; Caracterización: Opresivo, Punzante, Quemante, Eléctrico, Indefinido
(defrecord Characteristic [characteristic])
;; Duración: Agudo, Subagudo, Crónico
(defrecord Duration [duration])
;; Comportamiento: Constante, Fluctuante, Reacciona al estímulo
(defrecord Behaviour [behaviour])
;; Estímulo: Decúbito, Movilización, Palpación, Actividad física,
;; Expectoración, Reflejo nauseoso, Iluminación fuerte,
 ;; Sonidos fuertes, Infusión medicación endovenosa
(defrecord Stymulus [stymulus])
;; Reacción ante reposo: Cede, No cede
(defrecord RestReaction [rest-reaction])
;; Caída, Ningún evento traumático, Cirugía no ambulatoria,
;; Biomecánica inadecuada en el ámbito extrahospitalario,
;; Incorrecta movilización del paciente en el ámbito intrahospitalario...
(defrecord Context [context])

(def questions
  [{:question "¿Que intensidad siente de dolor?"
    :options ["Leve" :low
              "Moderada" :moderate
              "Alta" :high]
    :insertion ->Intensity}

   {:question "¿Siempre es el mismo lugar?"
    :options   ["Pecho" :chest
                "Cadera" :hip
                "Zona lumbar" :lumbar-area
                "Brazo" :arm
                "Zona posterior del cuello" :neck-back
                "Miembro superior" :upper-limbs
                "Cuadrante inferior derecho del abdomen" :right-inferior-adbomen-cuadrant
                "Cuadrante superior derecho del abdomen" :right-superior-abdomen-cuadrant
                "Cabeza" :head]
    :insertion ->Location}

   {:question "¿Siempre es el mismo lugar?"
    :options   ["si, siempre es el mismo lugar." true
                "no, el dolor se irradia hacia otras partes" false]
    :insertion ->Irradiation}

   {:question "¿Que caracteristica tiene el dolor?"
    :options ["Opresivo. Dolor en una región. Se siente como que está apretado o hinchado." :opresive
              "Punzante. Dolor muy localizado, se puede indicar con un dedo." :stinging
              "Quemante. " :burning
              "Eléctrico. Se puede sentir el recorrido del dolor." :electric
              "Indefinido. El paciente no sabe explicar, o es difícil explicar porque es mezcla de varios por ejemplo" :undefined]
    :insertion ->Characteristic}

   {:question "¿Cual es la duración?"
    :options ["Agudo. Corresponde al tiempo menor que las últimas 2 semanas." :acute
              "Subagudo. De 2 semanas a 3 meses." :subacute
              "Crónico. Más de 3 meses." :chronic]
    :insertion ->Duration}

   {:question "¿Siempre duele igual o a veces duele más o menos? ."
    :options ["constante" :constant
              "intermitente" :intermittent
              "reacciona a un estímulo" :triggered]
    :insertion ->Behaviour}

   {:question "¿Que estimulo dispara el dolor?"
    :options ["Posicional. ¿La persona puede acostarse o no, por ejemplo?" :position
              "Movilización." :mobilization
              "Al realizar fuerza." :strength
              "Al aumentar la actividad física (correr, caminar, etc.)" :activity
              "Expectoración (al toser)" :expectoration
              "Reflejo nauseoso / vómito." :reflex
              "Palpacion profunda" :deep-palpation
              "Iluminación." :ligthing
              "Sonidos fuertes" :loudness
              "Infusión de cierta medicación endovenosa." :intrafusion-infusion
              "Decúbito" :decubitus]
    :insertion ->Stymulus}
   {:question "¿Como reacciona al descanzo?"
    :options ["El dolor cede" :stop
              "El dolor no cede" :continue]
    :insertion ->RestReaction}
   {:question "¿En que contexto?"
    :options ["Caída" :fall
              "Ningún evento traumático" :none
              "Cirugía no ambulatoria" :non-ambulatory-surgery
              "Biomecánica inadecuada en el ámbito extrahospitalario" :extra-hospital
              "Incorrecta movilización del paciente en el ámbito intrahospitalario" :inner-hospital]
    :insertion ->Context}])

(defrecord Treatment [medicine description])

(def scale [#{"Paracetamol" "ibuprofeno" "ketorolac" "diclofenac"}
            #{"Dexametasona" "diazepam" "carbamazepina" "amitriptilina" "pregabalina" "gabapentina"}
            #{"Tramadol" "morfina" "fentanilo" "codeína"}])

(def rescue
  ["Morfina. 2 o 3 mg de morfina - es decir 2 o 3 ml de la solución preparada en la jeringa - administrado en bolo (tiempo de administración menor a 5 minutos) endovenoso."
   "Tramadol. 50 mg en 100 ml de solución fisiológica, vía endovenosa, durante 30 min a 1 h. Los rescates no deben superar los 200 mg de la droga en 24 hs."
   "Ketorolac. 10 mg o 20 mg vía oral o sublingual."])

(def treatments
  {1 [{:options ["Paracetamol oral 1 g cada 8 horas"
                 "Diclofenac oral 50 mg cada 8 horas"
                 "Diclofenac oral 75 mg cada 12 horas"
                 "Ketorolac oral 10 mg o 20 mg también cada 8 horas"]}]
   2 [{:options ["Diclofenac endovenoso 75 mg en 100 ml durante 1 hora cada 12 horas"
                 "Ketorolac endovenoso 30 mg en 100 ml durante 1 hora cada 8 horas"]}]
   3 [{:options ["Tramadol endovenoso 50 mg en 100 ml durante 1 hora cada 8 horas"]}]
   4 [{:treatments [1]}
      {:options ["Pregabalina oral 50 mg cada 8 horas"
                 "Pregabalina oral 75 mg cada 12 horas"
                 "Pregabalina oral 150 mg cada 24 horas"
                 "Amitriptilina oral 100 mg o 200 mg por día"]}
      {:options ["Diazepam oral de 5 mg a 10 mg cada 24 horas"
                 "Clonazepam oral o sublingual de 0,5 mg a 2 mg cada 24 horas"]}]
   5 [{:treatments [2]}
      {:options ["Pregabalina oral 50 mg cada 8 horas"
                 "Pregabalina oral 75 mg cada 12 horas"
                 "Pregabalina oral 150 mg cada 24 horas"
                 "Amitriptilina oral 100 mg o 200 mg por día"]}
      {:options ["Diazepam oral de 5 mg a 10 mg cada 24 horas"
                 "Clonazepam oral o sublingual de 0,5 mg a 2 mg cada 24 horas"]}]
   6 [{:treatments [3]}
      {:options ["Pregabalina oral 50 mg cada 8 horas"
                 "Pregabalina oral 75 mg cada 12 horas"
                 "Pregabalina oral 150 mg cada 24 horas"
                 "Amitriptilina oral 100 mg o 200 mg por día"]}
      {:options ["Diazepam oral de 5 mg a 10 mg cada 24 horas"
                 "Clonazepam oral o sublingual de 0,5 mg a 2 mg cada 24 horas"]}]})

(defrule coronary-acute-sindrome
  [Location (= location :chest)]
  [Characteristic (= characteristic :opresive)]
  [RestReaction (= rest-reaction :continue)]
  =>
  (insert! (->Treatment
            (treatments 3)
            "Es un diagnóstico presuntivo, el cual contempla 3 patologías cardiacas
con disminución de aporte de oxígeno al miocardio. Es un caso excepcional de emergencia,
donde se requiere acción inmediata.
Para establecer el diagnóstico definitivo se solicita un electrocardiograma
y muestras de laboratorio para marcadores cardiacos.
Durante la cateterización endovenosa se extrae sangre y
luego se continúa administrando tratamiento analgésico por esta vía.")))

(defrule humerus-fracture-1
  [Location (= location :arm)]
  [Characteristic (= characteristic :undefined)]
  [RestReaction (= rest-reaction :moderate)]
  =>
  (insert! (->Treatment
            (treatments 1)
            "El húmero es un hueso largo que es parte de la articulación del hombro.
Si la fractura se encuentra a niveles superiores,
el compromiso vasculonervioso es mayor,
por las relaciones anatómicas del húmero.
Por eso se precisa realizar diagnóstico por imagen e inmovilización de la región afectada.")))

(defrule humerus-fracture-2
  [Location (= location :arm)]
  [Characteristic (= characteristic :undefined)]
  [RestReaction (= rest-reaction :high)]
  =>
  (insert! (->Treatment
            (treatments 2)
            "El húmero es un hueso largo que es parte de la articulación del hombro.
Si la fractura se encuentra a niveles superiores,
el compromiso vasculonervioso es mayor,
por las relaciones anatómicas del húmero.
Por eso se precisa realizar diagnóstico
por imagen e inmovilización de la región afectada.
")))

(defrule femur-hip-fracture
  [Location (= location :hip)]
  [Context (= context :fall)]
  [RestReaction (= rest-reaction :high)]
  =>
  (insert! (->Treatment
            (treatments 2)
            "El fémur es un hueso largo que forma parte de la articulación de la cadera.
Esta región comprende la parte superior del hueso y con relaciones vasculonerviosas de gran calibre.
Debido a las relaciones anatómicas es imprescindible la inmovilización de la región afectada y diagnóstico por imagen.
La inmovilización implica que el paciente se encuentra acostado con las barandas elevadas por seguridad;
 debido a las necesidades de eliminación de excretas, se debe garantizar el confort del paciente en cuanto a la higiene en cama,
 así como prevenir úlceras por presión proporcionando rotaciones (cambio de posición).
")))

(defrule lumbalgia-1
  [Location (= location :lumbar-area)]
  [(=  true)]
  [Behaviour (= behaviour :intermittent)]
  [Duration (#{:subacute :chronic} duration)]
  [Intensity (= intensity :moderate)] =>
  (insert! (->Treatment
            (treatments 5)
            "De acuerdo al grado de afectación el dolor irradiará parcial o totalmente uno o ambos miembros inferiores.
No hay indicación de reposo estricto, sin embargo el paciente es quien establecerá los límites de su movilización
 según el umbral de tolerancia de dolor que maneje; es por eso que se deben garantizar medidas de confort y seguridad.
Se realiza diagnóstico por imagen (resonancia magnética y/o tomografía) para conocer la magnitud de afectación
y estructuras involucradas.
")))

(defrule lumbalgia-2
  [Location   (= location :lumbar-area)]
  [(=  true)]
  [Behaviour (= behaviour :intermittent)]
  [Duration (#{:subacute :chronic} duration)]
  [Intensity (= intensity :high)]
  =>
  (insert! (->Treatment
            (treatments 6)
            "De acuerdo al grado de afectación el dolor irradiará parcial o totalmente uno o ambos miembros inferiores.
No hay indicación de reposo estricto, sin embargo el paciente es quien establecerá los límites
 de su movilización según el umbral de tolerancia de dolor que maneje; es por eso que
se deben garantizar medidas de confort y seguridad. Se realiza diagnóstico por imagen
(resonancia magnética y/o tomografía) para conocer la magnitud de afectación y estructuras involucradas.")))

(defrule cervicalgia-c1-c4
  "Cervicalgia: niveles medulares cervicales superiores (C1 - C4)"
  [Intensity (#{:moderate :high} intensity)]
  [Location (= location :neck-back)]
  [Characteristic (#{:opresive :stinging} characteristic)]
  [Duration (#{:subacute :chronic} duration)]
  =>
  (insert! (->Treatment
            (treatments 5)
            "Se debe garantizar la seguridad y confort del paciente: barandas superiores elevadas,
cabecera a 30º, almohadas, posición de cuello y cabeza, rotación.
Se realiza diagnóstico por imagen (resonancia magnética y/o tomografía)
para conocer la magnitud de afectación y estructuras involucradas.")))

(defrule suspected-cerivical-fracture-1
  "Sospecha de fractura de vértebra cervical 1"
  [Intensity (= intensity :low)]
  [Location (= location :neck-back)]
  [Characteristic (#{:opresive :stinging} characteristic)]
  [Duration (= duration :acute)]
  =>
  (insert! (->Treatment
            (treatments 1)
            "Una alteración a este nivel puede comprometer toda la médula espinal,
 por lo que es fundamental y prioritaria la inmovilización del cuello (collar cervical).
 Se realiza diagnóstico por imagen (resonancia magnética y/o tomografía)
para conocer la magnitud de afectación y estructuras involucradas.")))

(defrule suspected-cervical-fracture-2
  "Sospecha de fractura de vértebra cervical 2"
  [Intensity (#{:high :moderate} intensity)]
  [Location (= location :neck-back)]
  [Characteristic (#{:opresive :stinging} characteristic)]
  [Duration (= duration :acute)]
  =>
  (insert! (->Treatment
            (treatments 2)
            "Una alteración a este nivel puede comprometer toda la médula espinal,
por lo que es fundamental y prioritaria la inmovilización del cuello (collar cervical).
Se realiza diagnóstico por imagen (resonancia magnética y/o tomografía) para conocer la magnitud de afectación y estructuras involucradas.")))

(defrule cervicalgia-c5-c8-1
  [Intensity (= intensity :low)]
  [Location (= location :upper-limbs)]
  [Duration (#{:subacute :chronic} duration)]
  [Context (= context :none)]
  =>
  (insert! (->Treatment
            (treatments 4)
            "Dependiendo de la magnitud del compromiso motor y si afecta a uno o ambos miembros superiores,
 el paciente será más o menos dependiente de los cuidados de enfermería. Se garantiza el confort y seguridad del paciente.
 Se realiza diagnóstico por imagen (resonancia magnética y/o tomografía) para conocer la magnitud de afectación y estructuras involucradas.")))

(defrule cervicalgia-c5-c8-2
  [Intensity (= intensity :moderate)]
  [Location (= location :upper-limbs)]
  [Duration (#{:subacute :chronic} duration)]
  [Context (= context :none)]
  =>
  (insert! (->Treatment
            (treatments 5)
            "Dependiendo de la magnitud del compromiso motor y si afecta a uno o ambos miembros superiores,
el paciente será más o menos dependiente de los cuidados de enfermería.
Se garantiza el confort y seguridad del paciente.
Se realiza diagnóstico por imagen (resonancia magnética y/o tomografía) para conocer la magnitud de afectación y estructuras involucradas.")))

(defrule recent-cefalea-1
  [Intensity (= intensity :low)]
  [Location (= location :head)]
  [Duration (= duration :acute)]
  =>
  (insert! (->Treatment
            (treatments 1)
            "La aparición en el tránsito de una internación o que sea el motivo de una consulta en guardia,
 que se asocia con otro síntoma motor en el momento de la valoración del dolor,
 o que este mismo aparezca después, condiciona la necesidad de realizar diagnóstico por imagen
(resonancia magnética y/o tomografía) para descartar un evento isquémico cerebral agudo (AIT, ACV).")))

(defrule recent-cefalea-2
  [Intensity (= intensity :moderate)]
  [Location (= location :head)]
  [Duration (= duration :acute)]
  =>
  (insert! (->Treatment
            (treatments 4)
            "La aparición en el tránsito de una internación o que sea el motivo de una consulta en guardia,
 que se asocia con otro síntoma motor en el momento de la valoración del dolor, o que este mismo aparezca después,
 condiciona la necesidad de realizar diagnóstico por imagen (resonancia magnética y/o tomografía) para descartar un evento isquémico cerebral agudo (AIT, ACV).")))

(defrule study-cefalea-1
  [Intensity (= intensity :moderate)]
  [Location (= location :head)]
  [Duration (#{:subacute :chronic} duration)]
  =>
  (insert! (->Treatment
            (treatments 5)
            "Este tipo de dolor crónico se define como incapacitante por lo que es necesario encontrar lo que lo provoque.
Durante el tiempo que tome la realización de estudios diagnósticos por imágenes
y laboratorio se establecerá un tratamiento para el dolor.")))

(defrule study-cefalea-2
  [Intensity (= intensity :high)]
  [Location (= location :head)]
  [Duration (#{:subacute :chronic} duration)]
  =>
  (insert! (->Treatment
            (treatments 6)
            "Este tipo de dolor crónico se define como incapacitante por
lo que es necesario encontrar lo que lo provoque.
Durante el tiempo que tome la realización de estudios
 diagnósticos por imágenes y laboratorio se establecerá un tratamiento para el dolor.")))

(defrule postoperation-inmediate-admision
  [Context (= context :non-ambulatory-surgery)]
  =>
  (insert! (->Treatment
            (treatments 2)
            "Un procedimiento quirúrgico implica la pérdida de integridad de estructuras anatómicas,
 tales como piel, órganos y/o músculos. En cualquier paciente que transita un postoperatorio
 inmediato se asume la presencia de dolor y la necesidad de aplicar tratamiento analgesico.")))

(defrule apendicitis
  [Location (= location :right-inferior-abdomen-cuadrant)]
  [Stymulus (= stymulus :deep-palpation)]
  =>
  (insert! (->Treatment
            (treatments 3)
            "El dolor es localizado y aumenta a la palpación profunda del punto doloroso de McBurney,
 lo cual orienta a la resolución quirúrgica y se solicitan estudios complementarios de rutina
 para darle solidez a la terapéutica. Luego de esta valoración (palpación) comienza el tratamiento para el dolor.")))

(defrule colecistitis
  [Location (= location :right-superior-abdomen-cuadrant)]
  [Stymulus (= stymulus :deep-palpation)]
  =>
  (insert! (->Treatment
            (treatments 3)
            "El dolor es localizado y aumenta a la palpación profunda en el punto doloroso cístico.
 Debido a las complejas relaciones anatómicas en esa región esta palpación no basta para dar diagnóstico
 por lo que se requieren estudios complementarios de laboratorio y diagnóstico por imágenes (ecografía).
Durante la realización de estudios comienza el tratamiento analgesico.")))

(defrule pericarditis
  "Pericarditis / taponamiento cardiaco"
  [Location (= location :chest)]
  [Characteristic (#{:opresive :stinging} characteristic)]
  [Stymulus (= stymulus :decubitus)]
  =>
  (insert! (->Treatment
            (treatments 3)
            "Debido a la fisiopatología, las manifestaciones clínicas y la región anatómica afectada,
 puede confundirse con otras patologías cardiacas de emergencia (síndrome coronario agudo / infarto agudo del miocardio)
 que hablan de la disminución del oxígeno al miocardio, pero que la resolución es muy distinta.
 El taponamiento cardiaco, que es posterior a la pericarditis, se considera como una emergencia.
 Se solicita un electrocardiograma y muestras de laboratorio como estudios complementarios.
El paciente va a preferir mantenerse sentado e inclinado hacia adelante.")))

(comment
  (defrule some
    [Intensity (= intensity nil)]
    [Location (= location nil)]
    [(= propagation nil)]
    [Characteristic (= characteristic nil)]
    [Duration (= duration nil)]
    [Behaviour (= behaviour nil)]
    [Stymulus (= stymulus nil)]
    [RestReaction (= rest-reaction nil)]
    [Context (= context nil)]
    =>
    (insert! (->Treatment
              (treatments 1)
              "
"))))

(defquery check-treatment
  "Query for treatment"
  []
  [?treatment <- Treatment])

(defsession session 'fallas1.rules)