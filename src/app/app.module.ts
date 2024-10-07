import {NgModule} from "@angular/core";
import {provideHttpClient} from "@angular/common/http";
import {AppComponent} from "./app.component";
import {BrowserModule} from "@angular/platform-browser";
import {PollComponent} from "./components/poll/poll.component";
import {PollOptionComponent} from "./components/poll-option/poll-option.component";

@NgModule({
  declarations: [
    AppComponent,
    PollComponent,
    PollOptionComponent
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
