%% 
%% This is file `thesis.cls',
%% generated with the docstrip utility.
%% 
%% The original source files were:
%% 
%% thesis.dtx  (with options: `thesis')
%% 
%% Copyright (C) 1991, 1996 by Wenzel Matiaske, mati1831@perform.ww.tu-berlin.de 
%% 
%% You are not allowed to distribute this file without the 
%% original source thesis.dtx. For distribution of the 
%% sources see the copyright notice in the file thesis.dtx. 
%% 
\def\filename{tesis}
\def\fileversion{1.0g}
\def\filedate{1996/25/01}
\def\docdate{1995/25/01}
\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{tesis}[\filedate\space\fileversion\space%
       LaTeX document class (wm).]
\newcommand\@ptsize{}
\newif\if@restonecol
\newif\if@titlepage
\@titlepagetrue
\newif\if@openright
\newif\if@openbib
\@openbibfalse
\newif\if@mainmatter \@mainmattertrue
\DeclareOption{a4paper}
   {\setlength\paperheight {297mm}%
    \setlength\paperwidth  {210mm}}
\DeclareOption{a5paper}
   {\setlength\paperheight {210mm}%
    \setlength\paperwidth  {148mm}}
\DeclareOption{b5paper}
   {\setlength\paperheight {250mm}%
    \setlength\paperwidth  {176mm}}
\DeclareOption{letterpaper}
   {\setlength\paperheight {11in}%
    \setlength\paperwidth  {8.5in}}
\DeclareOption{legalpaper}
   {\setlength\paperheight {14in}%
    \setlength\paperwidth  {8.5in}}
\DeclareOption{executivepaper}
   {\setlength\paperheight {10.5in}%
    \setlength\paperwidth  {7.25in}}
\DeclareOption{landscape}
   {\setlength\@tempdima   {\paperheight}%
    \setlength\paperheight {\paperwidth}%
    \setlength\paperwidth  {\@tempdima}}
\DeclareOption{10pt}{\renewcommand\@ptsize{0}}
\DeclareOption{11pt}{\renewcommand\@ptsize{1}}
\DeclareOption{12pt}{\renewcommand\@ptsize{2}}
\DeclareOption{oneside}{\@twosidefalse \@mparswitchfalse}
\DeclareOption{twoside}{\@twosidetrue  \@mparswitchtrue}
\newcount\hours \newcount\minutes
\def\SetTime{\hours=\time
        \global\divide\hours by 60
        \minutes=\hours
        \multiply\minutes by 60
        \advance\minutes by-\time
        \global\multiply\minutes by-1 }
\def\now{\number\hours:\ifnum\minutes<10 0\fi\number\minutes}
\newif\iffinal \finaltrue
\DeclareOption{draft}{\setlength\overfullrule{5pt}\finalfalse \SetTime}
\DeclareOption{final}{\setlength\overfullrule{0pt}\finaltrue}
\newif\if@thema       \@themafalse
\DeclareOption{thema}{\@thematrue}
\newif\if@crosshair       \@crosshairfalse
\DeclareOption{crosshair}{\@crosshairtrue}
\DeclareOption{nocrosshair}{\@crosshairfalse}
\newif\if@itemization     \@itemizationtrue
\DeclareOption{itemize}  {\@itemationtrue}
\DeclareOption{noitemize}{\@itemizationfalse}
\newif\if@enumeration       \@enumerationtrue
\DeclareOption{enumerate}  {\@enumerationtrue}
\DeclareOption{noenumerate}{\@enumerationfalse}
\newif\if@noind          \@noindfalse
\DeclareOption{indent}  {\@noindfalse}
\DeclareOption{noindent}{\@noindtrue}
\newif\if@center         \@centerfalse
\DeclareOption{center}  {\@centertrue}
\DeclareOption{nocenter}{\@centerfalse}
\newif\if@upper        \@upperfalse
\DeclareOption{upper}  {\@uppertrue}
\DeclareOption{noupper}{\@upperfalse}
\newif\if@headline        \@headlinetrue
\DeclareOption{headline}  {\@headlinetrue}
\DeclareOption{noheadline}{\@headlinefalse}
\newif\if@headcount        \@headcounttrue
\DeclareOption{headcount}  {\@headcounttrue}
\DeclareOption{noheadcount}{\@headcountfalse}
\def\partfont#1{\def\p@font{#1}}             \def\p@font{}
\def\chapterfont#1{\def\c@font{#1}}          \def\c@font{}
\def\chapterauthorfont#1{\def\ca@font{#1}}   \def\ca@font{}
\def\sectionfont#1{\def\s@font{#1}}          \def\s@font{}
\def\subsectionfont#1{\def\ss@font{#1}}      \def\ss@font{}
\def\subsubsectionfont#1{\def\sss@font{#1}}  \def\sss@font{}
\def\paragraphfont#1{\def\pg@font{#1}}       \def\pg@font{}
\def\subparagraphfont#1{\def\spg@font{#1}}   \def\spg@font{}
\def\titlefont#1{\def\t@font{#1}}            \def\t@font{}
\def\subtitlefont#1{\def\st@font{#1}}        \def\st@font{}
\def\authorfont#1{\def\a@font{#1}}           \def\a@font{}
\def\translatorfont#1{\def\a@font{#1}}       \def\tr@font{}
\def\institutionfont#1{\def\in@font{#1}}     \def\in@font{}
\def\theoremheaderfont#1{\def\thh@font{#1}}  \def\thh@font{}
\def\theorembodyfont#1{\def\thb@font{#1}}    \def\thb@font{}
\def\itemfont#1{\def\item@font{#1}}          \def\item@font{}
\def\examplefont#1{\def\ex@font{#1}}         \def\ex@font{}
\def\headingstextfont#1{\def\h@font{#1}}     \def\h@font{}
\def\pagenumberfont#1{\def\pn@font{#1}}      \def\pn@font{}
\def\captionheaderfont#1{\def\cph@font{#1}}  \def\cph@font{}
\def\captionbodyfont#1{\def\cpb@font{#1}}    \def\cpb@font{}
\def\figurefont#1{\def\fig@font{#1}}         \def\fig@font{}
\def\tablefont#1{\def\tab@font{#1}}          \def\tab@font{}
\def\indexsize#1{\def\index@size{#1}}        \def\index@size{}
\def\bibsize#1{\def\bib@size{#1}}            \def\bib@size{}
\DeclareOption{slanted}{
    \partfont{\Large}
    \chapterfont{\large\bf}
    \chapterauthorfont{\large}
    \sectionfont{\large\bf}%\slshape}
    \subsectionfont{\large\bf}
    \subsubsectionfont{}%\slshape}
    \paragraphfont{\slshape}
    \subparagraphfont{\slshape}
    \titlefont{\LARGE}
    \subtitlefont{\large}
    \authorfont{}
    \institutionfont{\slshape}
    \translatorfont{}
    \theoremheaderfont{\upshape}
    \theorembodyfont{}
    \itemfont{\slshape}
    \examplefont{}
    \headingstextfont{\small\slshape}
    \pagenumberfont{\small}
    \captionheaderfont{\slshape\small}
    \captionbodyfont{\small}
    \figurefont{}
    \tablefont{}
}
\DeclareOption{bold}{%
    \partfont{\LARGE\bfseries}
    \chapterfont{\LARGE\bfseries}
    \chapterauthorfont{\large}
    \sectionfont{\Large\bfseries}
    \subsectionfont{\large\bfseries}
    \subsubsectionfont{\bfseries}
    \paragraphfont{\bfseries}
    \subparagraphfont{\bfseries}
    \titlefont{\LARGE\bfseries}
    \subtitlefont{\large}
    \authorfont{}
    \translatorfont{}
    \institutionfont{\slshape}
    \theoremheaderfont{\bfseries}
    \theorembodyfont{\itshape}
    \itemfont{\bfseries}
    \examplefont{}
    \headingstextfont{\small\bfseries}
    \pagenumberfont{\small}
    \captionheaderfont{\bfseries}
    \captionbodyfont{}
    \figurefont{}
    \tablefont{}
}
\DeclareOption{sfbold}{%
    \partfont{\LARGE\sffamily\bfseries}
    \chapterfont{\LARGE\sffamily\bfseries}
    \chapterauthorfont{\Large\sffamily\bfseries}
    \sectionfont{\large\sffamily\bfseries}
    \subsectionfont{\large\sffamily\bfseries}
    \subsubsectionfont{\sffamily\bfseries}
    \paragraphfont{\sffamily\bfseries}
    \subparagraphfont{\sffamily}
    \titlefont{\LARGE\sffamily\bfseries}
    \subtitlefont{\large\sffamily}
    \authorfont{\Large\sffamily\slshape}
    \translatorfont{}
    \institutionfont{\sffamily}
    \theoremheaderfont{\sffamily}
    \theorembodyfont{}
    \itemfont{\sffamily}
    \examplefont{}
    \headingstextfont{\small\sffamily}
    \pagenumberfont{\small\rmfamily}
    \captionheaderfont{\sffamily}
    \captionbodyfont{}
    \figurefont{}
    \tablefont{}
}
\DeclareOption{titlepage}{\@titlepagetrue}
\DeclareOption{notitlepage}{\@titlepagefalse}
\DeclareOption{openright}{\@openrighttrue}
\DeclareOption{openany}{\@openrightfalse}
\DeclareOption{onecolumn}{\@twocolumnfalse}
\DeclareOption{twocolumn}{\@twocolumntrue}
\DeclareOption{leqno}{\input{leqno.clo}}
\DeclareOption{fleqn}{\input{fleqn.clo}}
\DeclareOption{openbib}{\@openbibtrue}
\newif\if@chapterbib \@chapterbibfalse
\DeclareOption{chapterbib}{\@chapterbibtrue}
\ExecuteOptions{letterpaper,10pt,oneside,onecolumn,final,openright,%
                slanted,center,upper}
\ProcessOptions*
\input{bk1\@ptsize.clo}
\setlength\lineskip{1\p@}
\setlength\normallineskip{1\p@}
\renewcommand\baselinestretch{}
\if@noind
  \setlength\parskip{0.5\baselineskip
         \@plus.1\baselineskip \@minus.1\baselineskip}
  \setlength\parindent{\z@}
  \def\noparskip{\par\vspace{-\parskip}}
\else
  \setlength\parskip{0\p@ \@plus 1\p@}
  \let\noparskip\relax
\fi
\@lowpenalty   51
\@medpenalty  151
\@highpenalty 301
\setcounter{topnumber}{2}
\renewcommand\topfraction{.7}
\setcounter{bottomnumber}{1}
\renewcommand\bottomfraction{.3}
\setcounter{totalnumber}{3}
\renewcommand\textfraction{.2}
\renewcommand\floatpagefraction{.5}
\setcounter{dbltopnumber}{2}
\renewcommand\dbltopfraction{.7}
\renewcommand\dblfloatpagefraction{.5}
\def\e@skip{\h@font{\phantom{y}}}
\if@twoside
  \def\ps@headings{
      \let\@oddfoot\@empty\let\@evenfoot\@empty
      \def\@evenhead{\vbox{\hsize=\textwidth
        \hbox to \textwidth{%
        {\pn@font\thepage}\hfill{\h@font\leftmark}\e@skip\if@center\hfill\fi}
        \if@headline \vskip 1.5pt \hrule \fi}}%
      \def\@oddhead{\vbox{\hsize=\textwidth
         \hbox to \textwidth{%
         \if@center\hfill\fi{\h@font\rightmark}\e@skip\hfill{\pn@font\thepage}}
         \if@headline \vskip 1.5pt \hrule \fi}}%
      \let\@mkboth\markboth
    \def\chaptermark##1{%
      \markboth {%
        \ifnum \c@secnumdepth >\m@ne
          \if@mainmatter
            \@chapapp\
              \if@headcount
                \thechapter. \ %
              \fi
          \fi
        \fi
        ##1}{}}%
    \def\sectionmark##1{%
      \markright {%
        \ifnum \c@secnumdepth >\z@
          \if@headcount
            \thesection. \ %
          \fi
        \fi
        ##1}}}
\else
  \def\ps@headings{%
    \let\@oddfoot\@empty
    \def\@oddhead{\vbox{\hsize=\textwidth
      \hbox to \textwidth{%
      \if@center\hfill\fi{\h@font\rightmark}\e@skip\hfill{\pn@font\thepage}}
      \if@headline \vskip 1.5pt \hrule \fi}}%
    \let\@mkboth\markboth
    \def\chaptermark##1{%
      \markright {%
        \ifnum \c@secnumdepth >\m@ne
          \if@mainmatter
            \@chapapp\
            \if@headcount
              \thechapter. \ %
            \fi
          \fi
        \fi
        ##1}}}
\fi
\def\ps@myheadings{%
    \let\@oddfoot\@empty\let\@evenfoot\@empty
    \def\@oddhead{\vbox{\hsize=\textwidth
      \hbox to \textwidth{%
      \if@center\hfill\fi{\h@font\rightmark}\e@skip\hfill{\pn@font\thepage}}
      \if@headline \vskip 1.5pt \hrule \fi}}%
    \def\@evenhead{\vbox{\hsize=\textwidth
      \hbox to \textwidth{%
      {\pn@font\thepage}\hfill{\h@font\leftmark}\e@skip\if@center\hfill\fi}
      \if@headline \vskip 1.5pt \hrule \fi}}%
    \let\@mkboth\@gobbletwo
    \let\chaptermark\@gobble
    \let\sectionmark\@gobble
    }
\def\ps@draft{%
    \let\@oddfoot\@empty\let\@evenfoot\@empty
    \def\@oddhead{\vbox{\hsize=\textwidth
      \hbox to \textwidth{%
      {\pn@font\today\ \now\ --- {\h@font \draftname: ``\jobname''}
       \hfil\e@skip \thepage}}
       \if@headline \vskip 1.5pt \hrule \fi}}%
    \def\@evenhead{\vbox{\hsize=\textwidth
       \hbox to \textwidth{%
       \pn@font\thepage\e@skip\hfil {\h@font \draftname: ``\jobname''} ---
       \today\ \now\ }
       \if@headline \vskip 1.5pt \hrule \fi}}%
    \let\@mkboth\@gobbletwo
    \let\chaptermark\@gobble
    \let\sectionmark\@gobble
    }
\@ifundefined{option@crosshair}{}{%
  \def\clap#1{\hbox to 0pt{\hss#1\hss}} \newdimen\@crosshairrule
  \@crosshairrule=.24pt \def\@crosshairs{\vbox to
    0pt{\hsize=0pt\baselineskip=0pt\lineskip=0pt \vss \clap{\vrule
        height .125in width \@crosshairrule depth 0pt} \clap{\vrule
        width .25in height \@crosshairrule depth 0pt} \clap{\vrule
        height .125in width \@crosshairrule depth 0pt} \vss}}
 \def\ps@empty{%
    \let\@oddfoot\@empty\let\@evenfoot\@empty
    \def\@oddhead{\hfill\raise\headheight\@crosshairs}
    \let\@evenhead\@oddhead}}
\def\@subtitle{}         \def\subtitle#1{\gdef\@subtitle{#1}}
\def\@translator{}       \def\translator#1{\gdef\@translator{#1}}
\def\@institution{}      \def\institution#1{\gdef\@institution{#1}}
\def\@dedication{}       \def\dedication#1{\def\@dedication{#1}}
\def\@uppertitleback{}   \long\def\uppertitleback#1{\def\@uppertitleback{#1}}
\def\@middletitleback{}  \long\def\middletitleback#1{\def\@middletitleback{#1}}
\def\@lowertitleback{}   \long\def\lowertitleback#1{\def\@lowertitleback{#1}}
\def\new@tpage{\newpage\thispagestyle{empty}\null}
\def\and{\end{tabular}\hskip 1em plus.17fil
   \if@center
      \begin{tabular}[t]{c}
   \else
     \begin{tabular}[t]{@{}l@{}}
   \fi}
\def\maketitle{%
  \begin{titlepage}
    \let\footnotesize\small
    \let\footnoterule\relax
    \def\thefootnote{\fnsymbol{footnote}}
     \if@twoside
       \new@tpage
       \begin{raggedright}
       {\t@font \@title \par}\vskip 1em
       {\st@font \@subtitle \par}\vspace{50pt}
       {\a@font \lineskip 1.25em
       \begin{tabular}[t]{@{}l@{}}
       \@author
       \end{tabular}\par}
       \vfill
       {\in@font\@institution\par}
       \end{raggedright}
       \new@tpage \new@tpage
       {\ss@font\@title}\par
       {\sss@font\@subtitle}
       \new@tpage
    \fi
    \new@tpage
     \begin{center}
     {\t@font\@title \par}\vskip 1em
     {\st@font\@subtitle \par}
     \vspace{50pt}
     {\a@font \lineskip 1.25em
     \begin{tabular}[t]{c}
     \@author
     \end{tabular} \par }\vfil
     {\tr@font \@translator \par}
     \vfil
     {\st@font \@date \par}
     \vskip 2em
     {\in@font\@institution \par}
     \end{center}
     \par \@thanks
   \if@twoside \new@tpage
      \noindent\begin{minipage}[t]{\textwidth}
      \@uppertitleback
      \end{minipage}\par
      \vfill
      \noindent\begin{minipage}[t]{\textwidth}
      \@middletitleback
      \end{minipage}\par
      \vfill
      \noindent\begin{minipage}[b]{\textwidth}
      \@lowertitleback
      \end{minipage}
   \fi
   \ifx\@dedication\@empty\else \new@tpage
        {\centering \Large \@dedication \par}
   \fi
   \if@twoside \new@tpage \fi
   \end{titlepage}
   \def\thefootnote{\arabic{footnote}}
   \setcounter{footnote}{0}
   \if@thema\else\let\thanks\relax\fi
   \gdef\@thanks{}\gdef\@author{}\gdef\@translator{}\gdef\@institution{}
   \gdef\@uppertitleback{}\gdef\@lowertitleback{}\gdef\@dedication{}%
   \gdef\@title{}\gdef\@subtitle{}\let\maketitle\relax}
\def\abstract{%\titlepage
  \chapter*{\abstractname\@mkboth{\abstractname}{\abstractname}}}
\def\endabstract{\par\vfil\null\endtitlepage}
\if@thema
   \if@center
     \def\chapterabstract{\if@twocolumn
        \subsection*{\abstractname}%
     \else \small
       \begin{center}%
        {\pg@font \abstractname\vspace{-.5em}\vspace{\z@}}%
        \end{center}%
        \quotation
     \fi}
     \def\endchapterabstract{\if@twocolumn\else\endquotation\fi}
   \else
     \def\chapterabstract{\if@twocolumn
        \subsection*{\abstractname}%
     \else \small
        \subsection*{\abstractname}%
     \fi}
     \def\endchapterabstract{\par\bigskip}
  \fi
\fi
\newcommand*{\chaptermark}[1]{}
\setcounter{secnumdepth}{2}
\newcounter {part}
\newcounter {chapter}
\newcounter {section}[chapter]
\newcounter {subsection}[section]
\newcounter {subsubsection}[subsection]
\newcounter {paragraph}[subsubsection]
\newcounter {subparagraph}[paragraph]
\renewcommand{\thepart}         {\Roman{part}}
\renewcommand{\thechapter}      {\arabic{chapter}}
\renewcommand{\thesection}      {\thechapter.\arabic{section}}
\renewcommand{\thesubsection}   {\thesection.\arabic{subsection}}
\renewcommand{\thesubsubsection}{\thesubsection .\arabic{subsubsection}}
\renewcommand{\theparagraph}    {\thesubsubsection.\arabic{paragraph}}
\renewcommand{\thesubparagraph} {\theparagraph.\arabic{subparagraph}}
\def\chapapp#1{\def\@chapapp{#1}}      \def\@chapapp{}
\newcommand{\frontmatter}{\cleardoublepage
            \@mainmatterfalse\pagenumbering{roman}}
\newcommand{\mainmatter}{\cleardoublepage
       \@mainmattertrue\pagenumbering{arabic}}
\newcommand{\backmatter}{\if@openright\cleardoublepage\else\clearpage\fi
      \@mainmatterfalse}
\newcommand{\part}{\cleardoublepage
    \thispagestyle{empty}%
    \if@twocolumn
        \onecolumn\@tempswatrue
     \else
       \@tempswafalse
     \fi
     \vspace*{\beforechaptervspace}%
     \secdef\@part\@spart}
\def\@part[#1]#2{%
    \ifnum \c@secnumdepth >-2\relax \refstepcounter{part}
      \addcontentsline{toc}{part}{\partname\ \thepart \hspace{1em}#1}%
    \else
     \addcontentsline{toc}{part}{#1}\fi \markboth{}{}%
     {\if@center
       \centering
      \else
       \raggedright
      \fi
     \reset@font
      \ifnum \c@secnumdepth >-2\relax
        \p@font \partname{} \thepart \par
         \vskip 20pt
      \fi
      \p@font
      \if@upper
        \uppercase{#2}
      \else #2
      \fi
  \par}
\@endpart}
\def\@spart#1{%
      {\if@center
          \centering
       \else
         \raggedright
       \fi
       \reset@font\p@font
       \if@upper
          \uppercase\expandafter{#1}
       \else #1%
       \fi\par}
  \@endpart}
\def\@endpart{\vfil\newpage
              \if@twoside
                \hbox{}%
                \thispagestyle{empty}%
                \newpage
              \fi
              \if@tempswa
                \twocolumn
              \fi}
\if@thema
  \def\chapterauthor#1{\gdef\@chapterauthor{#1}}  \def\@chapterauthor{}
  \def\shortauthor#1{\gdef\@shortauthor{#1}}      \def\@shortauthor{}
  \def\@makechapterauthor{\par
     \def\thefootnote{\fnsymbol{footnote}}%
     \def\@makefnmark{\hbox
         to\z@{$\m@th^{\@thefnmark}$\hss}}%
     \if@center
         \centering
     \else\parindent\z@
         \raggedright
     \fi
     {\ca@font  \lineskip .5em
     \if@center
         \begin{tabular}[t]{c}
     \else
         \begin{tabular}[t]{@{}l@{}}
     \fi
     \@chapterauthor
     \end{tabular} \par}\@thanks
     \vskip30\p@
     \gdef\@chapterauthor{}\gdef\@shortauthor{}
     \gdef\@thanks{}\setcounter{footnote}{0}}
\fi
\newcommand{\chapter}{\if@openright\cleardoublepage\else\clearpage\fi
                    \thispagestyle{plain}
                    \global\@topnum\z@
                    \@afterindentfalse
                    \secdef\@chapter\@schapter}
\def\@chapter[#1]#2{%
    \ifnum \c@secnumdepth >\m@ne
       \if@mainmatter
          \refstepcounter{chapter}%
          \typeout{\chaptername\space\thechapter.}
          \if@thema
             \ifx\@shortauthor\@empty
                \addcontentsline{toc}{chapter}{%
                \protect\numberline{\thechapter.}#1}%
             \else
                \addcontentsline{toc}{chapter}{%
                \protect\numberline{\thechapter.}%
                \@shortauthor\hfill\mbox{}\vskip\normallineskip #1}%
             \fi
          \else
             \addcontentsline{toc}{chapter}{%
             \protect\numberline{\thechapter.}#1}%
          \fi
      \else
        \addcontentsline{toc}{chapter}{#1}
      \fi
   \else
      \addcontentsline{toc}{chapter}{#1}
   \fi
   \chaptermark{#1}
   \addtocontents{lof}{\protect\addvspace{10pt}}
   \addtocontents{lot}{\protect\addvspace{10pt}}
   \if@twocolumn
      \@topnewpage[\@makechapterhead{#2}]
    \else
      \@makechapterhead{#2}
      \@afterheading
    \fi}
\newlength{\beforechaptervspace}
\setlength{\beforechaptervspace}{0pt}
\newlength{\afterchaptervspace}
\setlength{\afterchaptervspace}{30pt}
\def\@makechapterhead#1{%
  \vspace*{\beforechaptervspace}{%
  \ifx\@chapapp\@empty
     \if@center\centering
       \ifnum \c@secnumdepth >\m@ne
         {\c@font\thechapter.\ }
       \fi
       {\c@font
       \if@upper
          \uppercase{#1}
       \else
          #1
       \fi
       \par \nobreak}
     \else\raggedright
         \ifnum \c@secnumdepth >\m@ne
           \setbox\@tempboxa\hbox{\c@font\thechapter.\ %
           \c@font
           \if@upper
             \uppercase{#1}
           \else
              #1%
           \fi}
         \ifdim \wd\@tempboxa >\hsize
           \@hangfrom{%
             \c@font\thechapter.\ }{\c@font
           \if@upper
              \uppercase{#1}
           \else
              #1%
           \fi
           \par\nobreak}
         \else
           \hbox to\hsize{\box\@tempboxa\hfil}
         \fi
       \fi
     \fi
  \else
    \if@center
       \centering
     \else
       \raggedright
     \fi
     \ifnum \c@secnumdepth >\m@ne
       \if@mainmatter
          {\s@font \@chapapp{} \thechapter}
          \par \vskip 15\p@
       \fi
     \fi
     {\c@font
     \if@upper
        \uppercase{#1}
     \else
       #1%
     \fi
     \par \nobreak}
  \fi
  \vspace{\afterchaptervspace}
  \if@thema
    \ifx\@shortauthor\@empty
    \else
      \@mkboth{\@shortauthor}{#1}
    \fi
    \ifx\@chapterauthor\@empty
    \else
       \@makechapterauthor
    \fi
  \fi}
}
\def\@schapter#1{%
    \if@twocolumn
       \@topnewpage[\@makeschapterhead{#1}]
    \else
       \@makeschapterhead{#1}\@afterheading
    \fi}
\def\@makeschapterhead#1{%
   \vspace*{\beforechaptervspace}{%
     \if@center
        \centering
     \else
        \parindent\z@\raggedright
     \fi
     {\c@font
     \if@upper
       \uppercase\expandafter{#1}
     \else
        #1%
     \fi
     \par\nobreak
     \vskip 30\p@}
     \if@thema
       \ifx\@shortauthor\@empty
       \else
          \@mkboth{\@shortauthor}{#1}
       \fi
       \ifx\@chapterauthor\@empty
       \else
          \@makechapterauthor
       \fi\fi
     }}
\newcommand\section{\@startsection {section}{1}{\z@}%
                                   {-3.5ex \@plus -1ex \@minus -.2ex}%
                                   {2.3ex \@plus.2ex}%
                                   {%\if@center\centering\else\raggedright\fi
                                    \reset@font\s@font}}
\newcommand\subsection{\@startsection{subsection}{2}{\z@}%
                                     {-3.25ex\@plus -1ex \@minus -.2ex}%
                                     {1.5ex \@plus .2ex}%
                                     {%\if@center\centering\else\raggedright\fi
                                      \reset@font\ss@font}}
\newcommand\subsubsection{\@startsection{subsubsection}{3}{\z@}%
                                     {-3.25ex\@plus -1ex \@minus -.2ex}%
                                     {1.5ex \@plus .2ex}%
                                     {\if@center\centering\else\raggedright\fi
                                      \reset@font\sss@font}}
\newcommand\paragraph{\@startsection{paragraph}{4}{\z@}%
                                    {3.25ex \@plus1ex \@minus.2ex}%
                                    {-1em}%
                                    {\reset@font\pg@font}}
\newcommand\subparagraph{\@startsection{subparagraph}{5}{\parindent}%
                                       {3.25ex \@plus1ex \@minus .2ex}%
                                       {-1em}%
                                      {\reset@font\spg@font}}
\if@twocolumn
  \setlength\leftmargini  {2em}
\else
  \setlength\leftmargini  {2.5em}
\fi
\setlength\leftmarginii  {2.2em}
\setlength\leftmarginiii {1.87em}
\setlength\leftmarginiv  {1.7em}
\if@twocolumn
  \setlength\leftmarginv  {.5em}
  \setlength\leftmarginvi {.5em}
\else
  \setlength\leftmarginv  {1em}
  \setlength\leftmarginvi {1em}
\fi
\setlength\leftmargin    {\leftmargini}
\setlength  \labelsep  {.5em}
\setlength  \labelwidth{\leftmargini}
\addtolength\labelwidth{-\labelsep}
\@beginparpenalty -\@lowpenalty
\@endparpenalty   -\@lowpenalty
\@itempenalty     -\@lowpenalty
\if@enumeration
  \renewcommand\theenumi{\arabic{enumi}}
  \renewcommand\theenumii{\alph{enumii}}
  \renewcommand\theenumiii{\roman{enumiii}}
  \renewcommand\theenumiv{\Alph{enumiv}}
  \newcommand\labelenumi{\theenumi.}
  \newcommand\labelenumii{(\theenumii)}
  \newcommand\labelenumiii{\theenumiii.}
  \newcommand\labelenumiv{\theenumiv.}
  \renewcommand\p@enumii{\theenumi}
  \renewcommand\p@enumiii{\theenumi(\theenumii)}
  \renewcommand\p@enumiv{\p@enumiii\theenumiii}
\else
  \renewcommand\theenumi{\arabic{enumi}}
  \renewcommand\theenumii{\arabic{enumii}}
  \renewcommand\theenumiii{\arabic{enumiii}}
  \renewcommand\theenumiv{\arabic{enumiv}}
  \newcommand\labelenumi{\theenumi.}
  \newcommand\labelenumii{\theenumi.\theenumii.}
  \newcommand\labelenumiii{\theenumi.\theenumii.\theenumiii.}
  \newcommand\labelenumiv{\theenumi.\theenumii.\theenumiii.\theenumiv.}
  \renewcommand\p@enumii{\theenumi}
  \renewcommand\p@enumiii{\theenumi(\theenumii)}
  \renewcommand\p@enumiv{\p@enumiii\theenumiii}
\fi
\if@itemization
  \newcommand\labelitemi{$\m@th\bullet$}
  \newcommand\labelitemii{\normalfont\bfseries --}
  \newcommand\labelitemiii{$\m@th\ast$}
  \newcommand\labelitemiv{$\m@th\cdot$}
\else
  \newcommand\labelitemi{\bfseries --}
  \newcommand\labelitemii{\bfseries --}
  \newcommand\labelitemiii{\bfseries --}
  \newcommand\labelitemiv{\bfseries --}
\fi
\newenvironment{description}
               {\list{}{\labelwidth\z@ \itemindent-\leftmargin
                        \let\makelabel\descriptionlabel}}
               {\endlist}
\newcommand\descriptionlabel[1]{\hspace\labelsep
                                \item@font #1}
\newenvironment{verse}
               {\let\\=\@centercr
                \list{}{\itemsep      \z@
                        \itemindent   -1.5em%
                        \listparindent\itemindent
                        \rightmargin  \leftmargin
                        \advance\leftmargin 1.5em}%
                \item[]}
               {\endlist}
\newenvironment{quotation}
               {\list{}{\listparindent 1.5em%
                        \itemindent    \listparindent
                        \rightmargin   \leftmargin
                        \parsep        \z@ \@plus\p@}%
                \item[]}
               {\endlist}
\newenvironment{quote}
               {\list{}{\rightmargin\leftmargin}%
                \item[]}
               {\endlist}
\def\@begintheorem#1#2{\reset@font\thb@font\trivlist
      \item[\hskip \labelsep{\reset@font\thh@font #1\ #2:}]}
\def\@opargbegintheorem#1#2#3{\reset@font\thb@font\trivlist
      \item[\hskip \labelsep{\reset@font\thh@font #1\ #2\ (#3):}]}
\def\@endtheorem{\endtrivlist}
\newlength{\exampleindent}    \setlength{\exampleindent}{\parindent}
\newenvironment{example}%
   {\begin{list}{}{%
    \setlength{\leftmargin}{\exampleindent}}
    \ex@font \item[]}
   {\end{list}}
\newenvironment{describe}[1][\quad]%
  {\begin{list}{}{%
    \renewcommand{\makelabel}[1]{{\item@font ##1}\hfil}%
    \settowidth{\labelwidth}{{\item@font #1}}%
    \setlength{\leftmargin}{\labelwidth}%
    \addtolength{\leftmargin}{\labelsep}}}%
  {\end{list}}
\newenvironment{titlepage}
    {%
      \cleardoublepage
      \if@twocolumn
        \@restonecoltrue\onecolumn
      \else
        \@restonecolfalse\newpage
      \fi
      \thispagestyle{empty}%
      \if@compatibility
        \setcounter{page}{0}
      \fi}%
    {\if@restonecol\twocolumn \else \newpage \fi
    }
\newcommand\appendix{\par
  \setcounter{chapter}{0}%
  \setcounter{section}{0}%
  \ifx\@chapapp\@empty
    \def\@addappendixtocontents{\addcontentsline{toc}{part}{\appendixname}}
    \part*{\appendixname\@mkboth{\appendixname}{\appendixname}%%
           \@addappendixtocontents}
  \else
    \renewcommand{\@chapapp}{\appendixname}%
  \fi
  \renewcommand{\thechapter}{\Alph{chapter}}}
\setlength\arraycolsep{5\p@}
\setlength\tabcolsep{6\p@}
\setlength\arrayrulewidth{.4\p@}
\setlength\doublerulesep{2\p@}
\setlength\tabbingsep{\labelsep}
\skip\@mpfootins = \skip\footins
\setlength\fboxsep{3\p@}
\setlength\fboxrule{.4\p@}
\@addtoreset{equation}{chapter}
\renewcommand{\theequation}{\thechapter.\arabic{equation}}
\def\marginlabel#1{\marginpar%
   {\if@twoside
       \ifodd\c@page
          \raggedright
       \else
          \raggedleft
       \fi
     \else
        \raggedright
     \fi #1}}
\def\attention{\mbox{}%
    \marginpar[\raggedleft\large\bf! $\rightarrow$]%
        {\raggedright\large\bf $\leftarrow$ !}}
\def\seealso#1{\mbox{}%
    \marginpar[\raggedleft$\rightarrow$ \small #1]%
        {\raggedright\small  #1 $\leftarrow$}\ignorespaces}
\newcounter{figure}[chapter]
\renewcommand{\thefigure}{\thechapter.\@arabic\c@figure}
\def\fps@figure{tbp}
\def\ftype@figure{1}
\def\ext@figure{lof}
\def\fnum@figure{\figureshortname~\thefigure}
\newenvironment{figure}
               {\fig@font\@float{figure}}
               {\end@float}
\newenvironment{figure*}
               {\fig@font\@dblfloat{figure}}
               {\end@dblfloat}
\newcounter{table}[chapter]
\renewcommand{\thetable}{\thechapter.\@arabic\c@table}
\def\fps@table{tbp}
\def\ftype@table{2}
\def\ext@table{lot}
\def\fnum@table{\tableshortname~\thetable}
\newenvironment{table}
               {\tab@font\@float{table}}
               {\end@float}
\newenvironment{table*}
               {\tab@font\@dblfloat{table}}
               {\end@dblfloat}
\newlength\abovecaptionskip
\newlength\belowcaptionskip
\setlength\abovecaptionskip{10\p@}
\setlength\belowcaptionskip{0\p@}
\long\def\@makecaption#1#2{%
 \vskip\abovecaptionskip
 \setbox\@tempboxa\hbox{{\cph@font #1:} {\cpb@font #2}}%
 \ifdim \wd\@tempboxa >\hsize
    \@hangfrom{\cph@font #1: }{\cpb@font #2\par}%
 \else
    \hbox to\hsize{\hfil\box\@tempboxa\hfil}%
 \fi
 \vskip\belowcaptionskip}
\def\rb#1{\raisebox{1.5ex}[-1.5ex]{#1}}
\def\tablerule{\noalign{\ifnum0=`}\fi
   \hrule \@height \arrayrulewidth \vskip\doublerulesep
   \futurelet \@tempa\@xhline}
\def\thicktablerule{\noalign{\ifnum0=`}\fi
   \hrule \@height 2\arrayrulewidth \vskip\doublerulesep
   \futurelet \@tempa\@xhline}
\def\doubletablerule{\noalign{\ifnum0=`}\fi
   \hrule \@height \arrayrulewidth \vskip2\arrayrulewidth
   \hrule \@height \arrayrulewidth \vskip\doublerulesep
   \futurelet \@tempa\@xhline}
\def\thickhline{\noalign{\ifnum0=`}\fi
   \hrule \@height 2\arrayrulewidth\futurelet \@tempa\@xhline}
\def\doublehline{\noalign{\ifnum0=`}\fi
   \hrule \@height \arrayrulewidth\vskip2\arrayrulewidth
   \hrule \@height \arrayrulewidth \futurelet \@tempa\@xhline}
\DeclareOldFontCommand{\rm}{\normalfont\rmfamily}{\mathrm}
\DeclareOldFontCommand{\sf}{\normalfont\sffamily}{\mathsf}
\DeclareOldFontCommand{\tt}{\normalfont\ttfamily}{\mathtt}
\DeclareOldFontCommand{\bf}{\normalfont\bfseries}{\mathbf}
\DeclareOldFontCommand{\it}{\normalfont\itshape}{\mathit}
\DeclareOldFontCommand{\sl}{\normalfont\slshape}{\@nomath\sl}
\DeclareOldFontCommand{\sc}{\normalfont\scshape}{\@nomath\sc}
\DeclareRobustCommand*{\cal}{\@fontswitch{\relax}{\mathcal}}
\DeclareRobustCommand*{\mit}{\@fontswitch{\relax}{\mathnormal}}
\newcommand{\@pnumwidth}{1.55em}
\newcommand{\@tocrmarg} {2.55em}
\newcommand{\@dotsep}{4.5}
\setcounter{tocdepth}{2}

\newcommand{\tableofcontents}{%
    \if@twocolumn
      \@restonecoltrue\onecolumn
    \else
      \@restonecolfalse
    \fi
    \chapter*{\contentsname
        \@mkboth{\contentsname}%
                {\contentsname}}%
    \@starttoc{toc}%
    \if@restonecol\twocolumn\fi
    }
\newcommand*{\l@part}[2]{%
  \ifnum \c@tocdepth >-2\relax
    \addpenalty{-\@highpenalty}%
    \addvspace{2.25em \@plus\p@}%
    \begingroup
      \setlength\@tempdima{3em}%
      \parindent \z@ \rightskip \@pnumwidth
      \parfillskip -\@pnumwidth
      {\leavevmode
       {\sss@font #1}\hfil \hbox to\@pnumwidth{\hss #2}}\par
       \nobreak
         \global\@nobreaktrue
         \everypar{\global\@nobreakfalse\everypar{}}
    \endgroup
  \fi}
\newcommand*{\l@chapter}[2]{%
  \ifnum \c@tocdepth >\m@ne
    \addpenalty{-\@highpenalty}%
    \vskip 1.0em \@plus\p@
    \setlength\@tempdima{1.5em}%
    \begingroup
      \parindent \z@ \rightskip \@pnumwidth
      \parfillskip -\@pnumwidth
      \leavevmode
      \advance\leftskip\@tempdima
      \hskip -\leftskip
      {\sss@font #1}%
      \nobreak\leaders\hbox{%
         $\m@th \mkern \@dotsep mu.\mkern \@dotsep mu$}
      \hfill\nobreak\hbox to\@pnumwidth{\hfil\textrm{#2}}\par
      \penalty\@highpenalty
    \endgroup
  \fi}
\newcommand*{\l@section}      {\@dottedtocline{1}{1.5em}{2.3em}}
\newcommand*{\l@subsection}   {\@dottedtocline{2}{3.8em}{3.2em}}
\newcommand*{\l@subsubsection}{\@dottedtocline{3}{7.0em}{4.1em}}
\newcommand*{\l@paragraph}    {\@dottedtocline{4}{10em}{5em}}
\newcommand*{\l@subparagraph} {\@dottedtocline{5}{12em}{6em}}
\newcommand{\listoffigures}{%
    \if@twocolumn
      \@restonecoltrue\onecolumn
    \else
      \@restonecolfalse
    \fi
    \chapter*{\listfigurename
      \@mkboth{\listfigurename}%
              {\listfigurename}}%
    \@starttoc{lof}%
    \if@restonecol\twocolumn\fi
    }
\newcommand*{\l@figure}{\@dottedtocline{1}{1.5em}{2.3em}}
\newcommand{\listoftables}{%
    \if@twocolumn
      \@restonecoltrue\onecolumn
    \else
      \@restonecolfalse
    \fi
    \chapter*{\listtablename
      \@mkboth{\listtablename}%
              {\listtablename}}%
    \@starttoc{lot}%
    \if@restonecol\twocolumn\fi
    }
\let\l@table\l@figure
\newdimen\bibindent
\bibindent=1.5em
\newenvironment{thebibliography}[1]
     {\if@chapterbib
        \section*{\refname\@mkboth{\refname}{\refname}}%
      \else
        \chapter*{\bibname\@mkboth{\bibname}{\bibname}}%
      \fi
      \list{\@biblabel{\arabic{enumiv}}}%
           {
           \thispagestyle{plain}\parindent\z@
           \settowidth\labelwidth{\@biblabel{#1}}%
            \leftmargin\labelwidth
            \advance\leftmargin\labelsep
            \if@openbib
              \advance\leftmargin\bibindent
              \itemindent -\bibindent
              \listparindent \itemindent
              \parsep \z@
            \fi
            \usecounter{enumiv}%
            \let\p@enumiv\@empty
            \renewcommand\theenumiv{\arabic{enumiv}}}%
      \if@openbib
        \renewcommand\newblock{\par}
      \else
        \renewcommand\newblock{\hskip .11em \@plus.33em \@minus.07em}%
      \fi
      \sloppy\clubpenalty4000\widowpenalty4000%
      \sfcode`\.=\@m}
     {\def\@noitemerr
       {\@latex@warning{Empty `thebibliography' environment}}%
      \endlist}
\newcommand\newblock{}
\if@chapterbib
  \def\@mainbblfile{\jobname.bbl}
  \let\@bblfile=\@mainbblfile
  \def\bibliography#1{%
    \if@filesw\immediate\write\@auxout{\string\bibdata{#1}}\fi
    \@input{\@bblfile}}
  \def\@include#1 {\clearpage
    \if@filesw \immediate\write\@mainaux{\string\@input{#1.aux}}\fi
    \@tempswatrue
    \if@partsw \@tempswafalse\edef\@tempb{#1}
       \@for\@tempa:=\@partlist\do{\ifx\@tempa\@tempb\@tempswatrue\fi}
    \fi
    \if@tempswa
       \if@filesw \let\@auxout=\@partaux
           \immediate\openout\@partaux #1.aux
           \immediate\write\@partaux{\relax}
       \fi
       \def\@bblfile{#1.bbl}\@input{#1.tex}
       \let\@bblfile\@mainbblfile\clearpage
       \@writeckpt{#1}
       \if@filesw
          \immediate\closeout\@partaux
       \fi
       \let\@auxout=\@mainaux\else\@nameuse{cp@#1}
   \fi}
\fi
\newenvironment{theindex}
               {\if@twocolumn
                  \@restonecolfalse
                \else
                  \@restonecoltrue
                \fi
                \columnseprule \z@
                \columnsep 35\p@
                \twocolumn[\@makeschapterhead{\indexname}]%
                \@mkboth{\indexname}%
                        {\indexname}%
                \thispagestyle{plain}\parindent\z@
                \parskip\z@ \@plus .3\p@\relax
                \let\item\@idxitem\index@size}
               {\if@restonecol\onecolumn\else\clearpage\fi}
\newcommand{\@idxitem}  {\par\hangindent 40\p@}
\newcommand{\subitem}   {\par\hangindent 40\p@ \hspace*{20\p@}}
\newcommand{\subsubitem}{\par\hangindent 40\p@ \hspace*{30\p@}}
\newcommand{\indexspace}{\par \vskip 10\p@ \@plus5\p@ \@minus3\p@\relax}
\renewcommand\footnoterule{%
  \kern-3\p@
  \hrule width .4\columnwidth
  \kern 2.6\p@}
\@addtoreset{footnote}{chapter}
\if@noind
  \long\def\@makefntext#1{%
        \leftskip 2.0em%
        \noindent
        \hbox to 0em{\hss\@makefnmark\kern 0.25em}#1}
\else
  \long\def\@makefntext#1{%
      \parindent 1em%
      \noindent
      \hbox to 1.8em{\hss\@makefnmark\kern 0.25em}#1}
\fi
\def\contentsname{Contenidos}
\def\listfigurename{Lista de Figuras}
\def\listtablename{Lista de Tablas}
\def\bibname{Bibliograf\'ia}
\def\refname{Referencias}
\def\indexname{\'Indice}
\def\figurename{Figura}
\def\tablename{Tabla}
\def\chaptername{Cap\'itulo}
\def\appendixname{Ap\'endice}
\def\partname{Parte}
\def\abstractname{Abstract}
\def\draftname{preliminary draft}
\def\figureshortname{Fig.}                      % <-- thesis
\def\tableshortname{Tab.}                       % <-- thesis
\def\prefacename{Prefacio}                       % <-- thesis
\newcommand{\today}{\ifcase\month\or
  Enero\or Febrero\or Marzo\or Abril\or Mayo\or Junio\or
  Julio\or Augosto\or Septiembre\or Octubre\or Noviembre\or Diciembre\fi
  \space\number\day, \number\year}
\setlength\columnsep{10\p@}
\setlength\columnseprule{0\p@}
\iffinal
  \pagestyle{headings}
\else
  \pagestyle{draft}
\fi
\pagenumbering{arabic}
\if@twoside
\else
  \raggedbottom
\fi
\if@twocolumn
  \twocolumn
  \sloppy
  \flushbottom
\else
  \onecolumn
\fi
\endinput
%% 
%% End of file `thesis.cls'.
