# My Code Snippets

##spring/prototypeBeanFactoryExample

Spring OSGI registry doesn't support scope prototype beans.  Typical solution is to use a factory and the consuming beans call factory.createInstance() or something similar.  This also means that the consuming beans have to be injected with the factory and know to call createInstance().

Example creates a factory on in OSGI service but further wraps that factory with another factory extending from *org.springframework.beans.factory.FactoryBean*.   Spring will automatically inject the resulting beans as such the consuming beans need not know that a factory is involved.

##java/rpminputstream

Based on the RPM specs at this location.   Wraps RPM processing around InputStream interface.

http://refspecs.linuxbase.org/LSB_3.1.1/LSB-Core-generic/LSB-Core-generic/pkgformat.html

Responsible for processing RPM files to extract Lead, Signature, and Head sections.   Positions the inputstream to the beginning of payload.

Example of reading a file from the RPM
```
File file = new File("testrpm.rpm");
CpioArchiveInputStream cpioInputStream = RPMInputStream.getCpioArchiveInputStream(file);

CpioArchiveEntry cpioEntry;
while ((cpioEntry = cpioInputStream.getNextCPIOEntry()) != null) {
		if (cpioEntry.getName().contains("project.properties")) {
	                break;
		}
	}

Properties props = new Properties();
props.load(cpioInputStream);
```

##angular2/imageslider

Semantic-UI and Angular 2 based implementation of a simple image slider. 

Example Usage Template:
```
      <div *ngIf="hasScreenshots()" class="row">
        <div class="two wide column"> </div>
        <div class="twelve wide column">  
          <div id="slidercontainer" class="ui container">
            <imageslidercomponent               
              #imageslider [images]="images">           
            </imageslidercomponent>             
          </div>                        
        </div>                  
        <div class="two wide column"> </div>
      </div>            

      <div *ngIf="hasScreenshots()" class="row">
        <div class="two wide column"></div>
        <div class="twelve wide column">  
          <div class="ui container center aligned">
              <div class="ui icon buttons">             
                <button (click)="onPrev()" class="ui button">   
                  <i class="left arrow icon"></i>                       
                </button>                                       
                <button class="ui disabled button">             
                  {{imageIndex}}/{{imageTotal}}                         
                </button>                                       
                <button (click)="onNext()" class="ui button">   
                  <i class="right arrow icon"></i>                      
                </button>                                       
              </div>                                    
          </div>                        
        </div>                  
        <div class="two wide column"></div>
      </div> 

```

Example Usage Component:

The "images" array of type any[] can contain either URLs or Base64 strings.  The pushImageBase64 method handles appending the appropriate "data" metadata tag for the browser to process.

```
import { ImageSliderComponent } from 'imageslider/imageslider.component';

@Component({
...
  directives: [ ROUTER_DIRECTIVES, ImageSliderComponent ]
...
})
export class MySliderView {
...
  @ViewChild("imageslider")
  imageSlider: ImageSliderComponent;
  images: any[] = new Array();
  imageTotal: number;
  imageIndex: number;
...
  onPrev() {
    this.imageSlider.onPrev();
    this.updateImageCount();
  }     

  onNext() {
    this.imageSlider.onNext();
    this.updateImageCount();
  }     

  private updateImageCount() {
    if (this.hasScreenshots()) {
      this.imageTotal = this.imageSlider.getLength();
      this.imageIndex = this.imageSlider.getIndex()+1;
    }           
  } 

  hasScreenshots(): boolean { 
    if (this.images != undefined && this.images.length > 0) {
      return true;      
    }           

    return false; 
  } 
}
...
  private pushImageBase64(image: any) {
    if (image != undefined) {
      this.images.push("data:image/png;base64," + image);
    }           
  } 
```
