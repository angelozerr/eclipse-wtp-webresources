eclipse-wtp-webresources
===================

[![Build Status](https://secure.travis-ci.org/angelozerr/eclipse-wtp-webresources.png)](http://travis-ci.org/angelozerr/eclipse-wtp-webresources)

Eclipse WTP provides HTML editor with several completions (tags, CSS styles, etc) but it misses some features. Thoses features was created inside WTP bugzilla but WTP team seems very busy to implement it. The goal of this project is to provide some missing features and 
after try to contribute to WTP : 

 * CSS#class CSS#ID completion, hover, hyperlink. See [bug 302125](https://bugs.eclipse.org/bugs/show_bug.cgi?id=302125) and [bug 211190](https://bugs.eclipse.org/bugs/show_bug.cgi?id=211190) 
 * JS resources file completion, hover for script/@src
 * CSS resources file completion, hover for link/@href
 * Images resources file completion, hover for img/@src
   
# CSS#class CSS#ID

## Completion for CSS Class name

Provides completion for WTP HTML editor for CSS className : 

![Class completion](https://github.com/angelozerr/eclipse-wtp-webresources/wiki/images/ClassCompletion.png)

## Hyperlink for CSS Class name

Provides hyperlink for WTP HTML editor for CSS className : 

![Class hyperlink](https://github.com/angelozerr/eclipse-wtp-webresources/wiki/images/ClassHyperlink.png)

## Hover for CSS Class name

Provides text hover for WTP HTML editor for CSS className : 

![Class hover](https://github.com/angelozerr/eclipse-wtp-webresources/wiki/images/ClassHover.png)

# CSS files completion

![CSS File Completion](https://github.com/angelozerr/eclipse-wtp-webresources/wiki/images/CSSFileCompletion.png)

# Why org.eclipse.a.wst.htmlcss.ui?

Plugin is named with **org.eclipse.a.wst.htmlcss.ui**, why not **org.eclipse.wst.htmlcss.ui**?

It's because of CSS Hover. After debugging WTP, Hover is managed with BestMatchHover class, and it's not possible to sort hover coming from extension point. The used sort is the plugin id (see org\eclipse\wst\sse\ui\internal\extension\RegistryReader#orderExtensions) 

If plugin uses org.eclipse.wst.htmlcss.ui, it's org.eclipse.jst.jsf.facelet.ui.internal.hover.FaceletHover which is executed before CSS class hover and CSS class name hover doesn't work. If org.eclipse.a.wst.htmlcss.ui (before  org.eclipse.jst.jsf.facelet.ui) is executed correctly.

