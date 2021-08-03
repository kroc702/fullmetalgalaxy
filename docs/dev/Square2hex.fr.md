---
layout: default
title: Square textures to hex
published: true
lang: fr
categories: [dev]
---
# Methode 1
```
                           _
- texture de 1*1 de coté: |_|

- doublez sa taille et le nombre de motif (2*2) : _ _
                                                 |_|_|
                                                 |_|_|
                         
- rotation de 45° :  _  _
 coté de 2*2^0.5    | /\ |
                    |/\/\|
                    |\/\/|
                    |_\/_|

- redimentionnez la texture a (3*largeur hex) * (2*hauteur hex)  [le motif ce répette 1 fois]
        -> soit pour un hexagone de 77*40 (zoom tactique) 231*80
                    ou bien a (1*largeur hex) * (2*hauteur hex)
        -> soit pour un hexagone de 77*40 (zoom tactique) 77*80    [le motif ce répette 3 fois]

- découpez la forme hexagonal
```

# Methode 2
```
                           _
- texture de 1*1 de coté: |_|

- doublez sa taille et le nombre de motif (2*2) : _ _
                                                 |_|_|
                                                 |_|_|
                         
- redimentionnez la texture a (1.5*largeur hex) * (hauteur hex)  [le motif ce répette 2 fois]
        -> soit pour un hexagone de 77*40 (zoom tactique) 115*40

- découpez la forme hexagonal
```