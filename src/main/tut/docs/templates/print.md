---
layout: docs
title: "Print templates"
---

# Print
A Print template consists of a single template file:
* `body.html` is a Handlebars template for the content of the letter

### Address
Print templates must include a div element with the id set to letterAddress. 
This element has the following mandatory and optional fields:
* Mandatory	
    - line 1
    - postcode
    - town
    - county
* Optional
    - line 2
    - country
			

### Footers

To add footer for every page of a letter, the content of the footer has to be encapsulated by the <footer> tag. It is important that the <footer> element has to be the first element in the body. 

The bottom margin of the pages have to be offset by the height of the footer, by adding the `@page {margin-bottom: 30mm;}` css rule to the `<style>` element in the head of the template. For a 30mm high footer this will look the following: 

```
<head>
    <meta charset="utf-8" />

    <style>
        @page {
            margin-bottom: 30mm;
        }
    </style>
</head>

```

### Page Breaks
As the content of the letter is broke into pages when rendering, in case the last element on a page exceeds the remaining space at the bottom of the page, the element is split and overflows to the next page. 

By adding the `break-inside: avoid` parameter to the style of the element we can avoid the element being split over two pages and enforce the entire element to be placed onto the next page. 

### Colours
Letters are printed on industrial printers which work with CMYK colour encoding. 

To ensure that the printed document stylistically consistent, colours in print templates have to be CMYK encoded. For example: 
```
h1 {
    color: cmyk(83%, 10%, 100%, 1%);
    font-weight:normal;
    font-size: 18px;
}

.infoBox {
    border-radius: 10px;
    border: solid cmyk(30%, 0%, 40%, 0%) 2px;
    padding: 10px;
    padding-top: 0px;
}
```

### Assets
Similarly to email, a print template can also reference optional assets, however images have to meet the following criterias:  
 - encoded in one of the following CMYK colourspace supporting formats: tif, tiff, jpg, jpeg. 
 - its size is set to ensure that it fits onto a page, if an image is wider or higher than the page, the template will be rejected.

Templates cannot contain scripts and cannot reference third party stylesheets, however stylesheets can be uploaded as assets.

## Use Cases

###### Ovo Contract letter:

<img src="https://s3-eu-west-1.amazonaws.com/dev-ovo-comms-template-assets/samples/ovoContract.jpg" width="231" height="319" style="border: 1px solid lightgray">

[letter](https://s3-eu-west-1.amazonaws.com/dev-ovo-comms-template-assets/samples/ovoContract.pdf) | [html](https://s3-eu-west-1.amazonaws.com/dev-ovo-comms-template-assets/samples/ovoContractHtml) | [css](https://s3-eu-west-1.amazonaws.com/dev-ovo-comms-template-assets/samples/ovoContractCSS) | [zip](https://s3-eu-west-1.amazonaws.com/dev-ovo-comms-template-assets/samples/ovoContract.zip)


###### Boost Welcome letter: 

<img src="https://s3-eu-west-1.amazonaws.com/dev-ovo-comms-template-assets/samples/boostWellcome.jpg" width="231" height="319" style="border: 1px solid lightgray">

[letter](https://s3-eu-west-1.amazonaws.com/dev-ovo-comms-template-assets/samples/boostWellcome.pdf) | [html](https://s3-eu-west-1.amazonaws.com/dev-ovo-comms-template-assets/samples/boostWellcomeHtml) | [css](https://s3-eu-west-1.amazonaws.com/dev-ovo-comms-template-assets/samples/boostWellcomeCSS) | [zip](https://s3-eu-west-1.amazonaws.com/dev-ovo-comms-template-assets/samples/boostWellcome.zip)
