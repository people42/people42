import { Card } from "../../../../components/index";
import { ProgressBar } from "../index";
import React, { ReactElement } from "react";
import styled from "styled-components";

interface signUpCardProps {
  step: 1 | 2 | 3;
  title: string;
  content: ReactElement;
}

function SignUpCard({ step, title, content }: signUpCardProps) {
  return (
    <StyledSignUpCard>
      <section>
        <Card isShadowInner={false}>
          <div className="card-box">
            <ProgressBar step={step} />
            <h1>{title}</h1>
            {content}
          </div>
        </Card>
      </section>
    </StyledSignUpCard>
  );
}

export default SignUpCard;

const StyledSignUpCard = styled.div`
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  & .card-box {
    height: 100%;
    padding: 24px;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
  }
  & > section {
    width: 420px;
    height: 420px;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  & h1 {
    ${({ theme }) => theme.text.header5};
    text-align: center;
    margin-bottom: 16px;
    animation: floatingUp 0.3s;
  }
`;
