What is Eclipse WTP WebResources?
===================

[![Build Status](https://secure.travis-ci.org/angelozerr/eclipse-wtp-webresources.png)](http://travis-ci.org/angelozerr/eclipse-wtp-webresources)

Eclipse WTP provides HTML editor with several completions (tags, CSS styles declaration, etc) but it misses some features about Web resources (CSS, JavaScript, Images). Those features was created inside WTP bugzilla but WTP team seems very busy to implement it. The goal of this project is to provide the missing features about Web resources and after try to contribute to WTP : 

 * CSS#class CSS#ID completion, hover, hyperlink. See [bug 302125](https://bugs.eclipse.org/bugs/show_bug.cgi?id=302125) and [bug 211190](https://bugs.eclipse.org/bugs/show_bug.cgi?id=211190).
 * JS resources files completion, hover for script/@src.
 * CSS resources files completion, hover for link/@href.
 * Images resources files completion, hover for img/@src.
   
# CSS#class & CSS#ID

## Completion for CSS Class name

Eclipse WTP WebResources provides completion for WTP HTML editor for CSS className : 

![Class completion](https://github.com/angelozerr/eclipse-wtp-webresources/wiki/images/ClassCompletion.png)

and for CSS ID : 

![CSS ID completion](https://github.com/angelozerr/eclipse-wtp-webresources/wiki/images/CSSIDCompletion.png)

## Hyperlink for CSS Class name

Eclipse WTP WebResources provides hyperlink for WTP HTML editor for CSS className : 

![Class hyperlink](https://github.com/angelozerr/eclipse-wtp-webresources/wiki/images/ClassHyperlink.png)

## Hover for CSS Class name

Provides text hover for WTP HTML editor for CSS className : 

![Class hover](https://github.com/angelozerr/eclipse-wtp-webresources/wiki/images/ClassHover.png)

# CSS files completion

![CSS File Completion](https://github.com/angelozerr/eclipse-wtp-webresources/wiki/images/CSSFileCompletion.png)

## Images completion & hover

![imagehover](https://cloud.githubusercontent.com/assets/1932211/4271513/01b62afe-3cd9-11e4-8cb0-3b1ddc5005f6.png)

and completion : 

![imagecompletion](https://cloud.githubusercontent.com/assets/1932211/4271537/43276e44-3cd9-11e4-9475-947a0bb25ef8.png)

# Installation

Eclipse WTP Web Resources is developed/tested with Eclipse 4.4 Luna. It is advised to use Eclipse 4.4 Luna (even if it could work with older version of Eclipse).

To install Eclipse WTP Web Resources, please read [Installation - Update Site](https://github.com/angelozerr/eclipse-wtp-webresources/wiki/Installation-Update-Site) section.

# Why org.eclipse.a.wst.webresources.ui?

See [bug 444189](https://bugs.eclipse.org/bugs/show_bug.cgi?id=444189)

Plugin is named with **org.eclipse.a.wst.webresources.ui**, why not **org.eclipse.wst.webresources.ui**?

It's because of CSS Hover. After debugging WTP, Hover is managed with BestMatchHover class, and it's not possible to sort hover coming from extension point. The used sort is the plugin id (see org\eclipse\wst\sse\ui\internal\extension\RegistryReader#orderExtensions) 

If plugin uses org.eclipse.wst.webresources.ui, it's org.eclipse.jst.jsf.facelet.ui.internal.hover.FaceletHover which is executed before CSS class hover and CSS class name hover doesn't work. If org.eclipse.a.wst.webresources.ui (before  org.eclipse.jst.jsf.facelet.ui) is executed correctly.

# Build

Eclipse WTP Web Resourcese is build with this [cloudbees job](https://opensagres.ci.cloudbees.com/job/eclipse-wtp-webresources/).
