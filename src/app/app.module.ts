import {NgModule} from "@angular/core";
import {provideHttpClient} from "@angular/common/http";
import {AppComponent} from "./app.component";
import {BrowserModule} from "@angular/platform-browser";

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule
  ],
  providers: [
    provideHttpClient()
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
