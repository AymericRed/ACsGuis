

ACsGuis permet d'utiliser des feuilles de style CSS pour styliser les guis Minecraft.

Le gui peut être créé soit en utilisant du code java, avec une API basée sur celle de Reden, soit en utilisant Sqript.

- Voir des exemples de guis codés en java (lien)
- Tutoriel Sqript (lien)

Pour ce qui est du style, les propriétés CSS les plus courantes sont supportées, avec toutefois beaucoup de différentes avec le CSS officiel. Ainsi le placement des composants se fait uniquement avec les propriétés "left, right, top et bottom", ou avec les layouts (lien).

- Voir la liste des propriétés CSS supportées.



@FontFace supportée

Couleurs : ...

Héritage...

Auto style....

CSS : Toutes celles non mentionnées ne seront pas reconnues.

| Nom                     | Description                                                  | Exemples                  |
| ----------------------- | ------------------------------------------------------------ | ------------------------- |
| background-color        | Couleur de l'arrière-plan                                    | background-color: orange; |
| background-image        | Texture de l'arrière-plan                                    | TODO                      |
| background-repeat       | Répétion de la texture de l'arrière plan                     | Voir doc officielle css   |
| color                   | Couleur du texte                                             | color: rgb(30, 150, 80);  |
| visibility              | Visibilité de composant : "hidden" le cachera, sinon il sera toujours visible | visibility: hidden;       |
| font-size               | Taille du texte                                              | font-size: 15px;          |
| font-style              | Style du texte ('italic' ou 'normal')                        |                           |
| font-family             | Police d'écriture, voir @FontFace                            |                           |
| border-color            | Couleur des bordures                                         |                           |
| border-width            | Largeur des bordures                                         | border-width: 2px;        |
| border-position         | Position des bordures ('internal' ou 'external')             |                           |
| border-radius           | Rayon des bordures, pour faire des bords arrondis            | border-radius: 5px;       |
| text-shadow             | 'enable' : Active l'ombre du texte                           |                           |
| z-index                 | Position du composant en profondeur, sans unité              | z-index: 50;              |
| padding-left            | Marge à gauche                                               |                           |
| padding-top             | Marche en haut                                               |                           |
| padding-right           | Marge à droit                                                |                           |
| paddin-bottom           | Marge en bas                                                 |                           |
| text-align              | Alignement horizontal du texte ('left', 'right' ou 'center') |                           |
| text-align-vertical     | Alignement vertical du texte ('top', 'bottom' ou 'center')   |                           |
| component-layout        | Layout du composant, voir plus haut                          |                           |
| width                   | Largeur du composant                                         |                           |
| max-width               | Largeur maximum du composant                                 |                           |
| min-width               | Largeur minimum du composant                                 |                           |
| height                  | Hauteur du composant                                         |                           |
| max-height              | Hauteur maximale du composant                                |                           |
| min-height              | Hauteur minimale du composant                                |                           |
| left                    | Position à gauche du composant                               | Voir plus haut            |
| right                   | Position à droite du composant                               | Voir plus haut            |
| top                     | Position en haut du composant                                | Voir plus haut            |
| bottom                  | Position en bas du composant                                 | Voir plus haut            |
| horizontal-position     | 'center' pour mettre le composant au centre horizontalement  |                           |
| vertical-position       | 'center' pour mettre le composant au centre verticalement    |                           |
| progress-bar-full-image | Image de fond d'une barre de progression pleine              | Voir 'background-image'   |
| progress-bar-full-color | Couleur de fond d'une barre de progression pleine            | Voir 'background-color'   |
| progress-bar-text-color | Couleur du texte d'une barre de progression                  | Voir 'color'              |