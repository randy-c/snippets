import { Component, Input, ElementRef, AfterViewInit, OnInit } from '@angular/core';

declare var jQuery: any;

/**
https://github.com/randy-c


Image slider component.

Input:

	images: any[] - images in slider.
	size: size - size of image.	

Methods:

	onPrev() - previous image
	onNext() - next image

**/

@Component({
	selector: 'imageslidercomponent',
	template: `
		<img id="curimage" class="ui centered {{size}} image" [attr.src]="activeimage"/>
	`
}) 
export class ImageSliderComponent implements AfterViewInit, OnInit {
	@Input() images: any[] = new Array();
	@Input() size = "huge";
	activeimage: any;
	index: number;
	curimage: any;

	constructor(private elementRef: ElementRef) {}

	onPrev() {
		if (this.index <= 0) {
			this.index = this.images.length-1;	
		} else {
			this.index--;
		}
		
		this.curimage.transition('slide left');	

		window.setTimeout(() => { 
				this.activeimage = this.images[this.index];
			}, 200)

		this.curimage.transition('slide right');	
	}

	onNext() {
		if (this.index >= this.images.length-1) {
			this.index=0;
		} else {
			this.index++;
		}

		this.curimage.transition('slide right');	

		window.setTimeout(() => { 
				this.activeimage = this.images[this.index];
			}, 200)

		this.curimage.transition('slide left');	
	}
	
	getLength(): number {
		return this.images.length;
	}

	getIndex(): number {
		return this.index;
	}

	ngAfterViewInit() { 
		this.curimage = jQuery(this.elementRef.nativeElement).find('#curimage');
	}

	ngOnInit() {
		this.index=0;
		this.activeimage = this.images[0];
	}
}
