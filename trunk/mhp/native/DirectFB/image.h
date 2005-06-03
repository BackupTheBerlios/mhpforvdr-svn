

class Image {
public:
     Image();
     IDirectFBSurface*    surface;
     DFBImageDescription  desc;

     int                  hasalpha;
     int                  left, top;     /* some GIF movies need drawing offsets */

     short                latency;  /* between image flips, for "gif-movies" */
     short                frame;         /* frame number for animations */
     struct Image*        next;         /* next movie-frame */
};
