import {Component} from '@angular/core';
import {PollApiService} from "./services/poll-api.service";
import {Poll} from "./model/poll";
import {CookieApiService} from "./services/cookie-api.service";

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrl: 'app.component.scss'
})
export class AppComponent {

  loading: boolean = true;
  poll?: Poll;
  submitDisabled = false;
  message?: string;

  constructor(
      private cookieApi: CookieApiService,
      private pollApi: PollApiService
  ) {
    this.cookieApi.getCookie().subscribe(() => {
      this.pollApi.getActivePoll().subscribe(poll => {
        this.poll = poll;
        this.loading = false;
      }, err => {
        console.error(err);
        this.message = 'No polls available, please check later';
        this.loading = false;
      });
    });
  }

  submit() {
    if (this.poll === undefined) return;
    const optionId = this.poll.options.find(option => option.userSelection)?.id;
    if (optionId === undefined) return;

    this.submitDisabled = true;
    this.pollApi.submitPoll(this.poll.id, optionId).subscribe(updatedPoll => {
      this.poll = updatedPoll;
      this.message = 'Thank you for your response!'
      this.submitDisabled = false;
    }, () => {
      this.submitDisabled = false; // re-enable button on error
    });
  }

}
