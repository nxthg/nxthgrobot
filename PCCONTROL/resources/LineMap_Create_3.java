import lejos.robotics.mapping.LineMap;
import lejos.geom.Rectangle;
import lejos.geom.Line;

/**
  *
  * description
  *
  * @version 1.0 from 14.05.2013
  * @author 
  */

public class LineMap_Create_3 {  
  public static void main(String[] args) throws InterruptedException {
    Rectangle boundingRect = new Rectangle (0f,0f,407.4f,558f);                       // Angaben in cm    
    Line[] lines = new Line[48];
    lines[0] = new Line(0f,0f,0f,136f);
    lines[1] = new Line(0f,136f,55.7f,136f);
    lines[2] = new Line(55.7f,136f,72.2f,100.7f);
    lines[3] = new Line(72.2f,100f,82f,100.7f);
    lines[4] = new Line(82f,100.7f,82f,165.6f);
    lines[5] = new Line(82f,165.6f,117.4f,182f);
    lines[6] = new Line(117.4f,182f,117.4f,197.7f);                   
    lines[7] = new Line(117.4f,197.7f,82f,214f);
    lines[8] = new Line(82f,214f,82f,295.6f);
    lines[9] = new Line(82f,295.6f,117.4f,312f);
    lines[10] = new Line(117.4f,312f,118.4f,337.8f);
    lines[11] = new Line(118.4f,337.8f,101f,355.3f);
    lines[12] = new Line(101f,355.3f,82.5f,364f);                
    lines[13] = new Line(82.5f,364f,82f,458f);
    lines[14] = new Line(82f,458f,85.6f,498.5f);
    lines[15] = new Line(85.6f,498.5f,85.6f,537.3f);
    lines[16] = new Line(85.6f,537.3f,130f,558f);
    lines[17] = new Line(130f,558f,170f,558f);
    lines[18] = new Line(170f,558f,214.5f,537.3f);
    lines[19] = new Line(214.5f,537.3f,214.5f,498.5f);
    lines[20] = new Line(214.5f,498.5f,218.3f,458f);           
    lines[21] = new Line(218.3f,458f,218.3f,365f);
    lines[22] = new Line(218.3f,365f,199f,355.5f);
    lines[23] = new Line(199f,355.5f,182f,337.5f);
    lines[24] = new Line(182f,337.5f,182f,312.5f);
    lines[25] = new Line(182f,312.5f,218f,295.6f);
    lines[26] = new Line(218f,295.6f,218f,214.4f);
    lines[27] = new Line(218f,214.4f,182.6f,197.5f);
    lines[28] = new Line(182.6f,197.5f,182.6f,182f);
    lines[29] = new Line(182.6f,182f,218f,165.6f);         
    lines[30] = new Line(218f,165.6f,218f,100.7f);
    lines[31] = new Line(218f,100.7f,227.8f,100.7f);
    lines[32] = new Line(227.8f,100.7f,244.5f,136f);
    lines[33] = new Line(244.5f,136f,389.8f,136.2f);
    lines[34] = new Line(389.8f,136.2f,407.4f,98f);
    lines[35] = new Line(407.4f,98f,407.4f,38f);
    lines[36] = new Line(407.4f,38f,389.8f,0f);
    lines[37] = new Line(389.8f,0f,244.4f,0f);
    lines[38] = new Line(244.4f,0f,228f,35.3f);          
    lines[39] = new Line(228f,35.3f,218f,35.3f);
    lines[40] = new Line(218f,35.3f,218.3f,22.2f);
    lines[41] = new Line(218.3f,22.2f,180f,4.6f);
    lines[42] = new Line(180f,4.6f,120f,4.6f);
    lines[43] = new Line(120f,4.6f,82f,22.2f);
    lines[44] = new Line(82f,22.2f,82f,35.3f);
    lines[45] = new Line(82f,35.3f,72.4f,35.3f);
    lines[46] = new Line(72.4f,35.3f,55.8f,0f);
    lines[47] = new Line(55.8f,0f,0f,0f);
    
    
    LineMap linemap = new LineMap (lines, boundingRect);
    try { 
      linemap.createSVGFile("ISS.svg"); 
    } catch(Exception e) {                      
      System.out.println(e);
    } finally {
      
    } // end of try
  }
}
